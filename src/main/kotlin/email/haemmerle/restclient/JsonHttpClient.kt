package email.haemmerle.restclient

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import org.apache.logging.log4j.LogManager
import java.nio.charset.StandardCharsets

class JsonHttpClient(val hostBaseUrl: String, val authorizer: AuthorizationMethod = NullAuthorizer) {

    init {
        authorizer.initialize(this)
    }

    companion object {
        private val logger = LogManager.getLogger()

        private const val CONTENTTYPE_JSON_UTF8 = "application/json;charset=utf-8"
        private const val ENCODING_GZIP_DEFLATE = "gzip, deflate"
        private const val ONE_MINUTE_IN_MILISECONDS = 60000
    }

    class Headers {
        companion object {
            const val CONTENT_TYPE = "Content-Type"
            const val COOKIE = "Cookie"
            const val ACCEPT = "Accept"
            const val ACCEPT_ENCODING = "Accept-Encoding"
            const val SET_COOKIE = "Set-Cookie"
        }
    }

    fun handleFailure(response: Response, result: Result<ByteArray, FuelError>) {
        if (response.statusCode == -1) {
            throw ServerUnreachable("$hostBaseUrl ${response.url}")
        }
        if (response.statusCode == 500) {
            throw UnexpectedServerError("Check Server ($hostBaseUrl) log for further information.")
        }
        if (result is Result.Failure) {
            if (response.header(Headers.CONTENT_TYPE).first().replace(" ", "") == CONTENTTYPE_JSON_UTF8) {
                val ResponseObject = Klaxon().parse<ResponseObject>(response.data.toString(StandardCharsets.UTF_8))
                throw HttpRequestFailed("Request to ${response.url} responds: ${ResponseObject?.errorText().orEmpty()}")
            } else {
                throw HttpRequestFailed("Request to ${response.url} responds: ${response.data.toString(StandardCharsets.UTF_8)}")
            }
        }
    }

    fun performJsonRequest(path: String, body: String = ""): ByteArray {
        return  if (body.isEmpty()) performJsonGetRequest(path)
                else performJsonPostRequest(path, body)
    }

    inline fun <reified T> performJsonGetRequest(path: String): T? {
        return Klaxon().parse(performJsonGetRequest(path).toString(Charsets.UTF_8))
    }

    fun performJsonGetRequest(path: String): ByteArray {
        val request = "${hostBaseUrl}${path}".httpGet()
                .header(Headers.ACCEPT to CONTENTTYPE_JSON_UTF8)
                .header(Headers.ACCEPT_ENCODING to ENCODING_GZIP_DEFLATE)
                .timeout(ONE_MINUTE_IN_MILISECONDS)
        authorizer.authorize(request)
        val (_, response, result) = request.response()
        handleFailure(response, result)
        return result.get()
    }

    fun performJsonPostRequest(path: String, body: String = ""): ByteArray {
        val request = "${hostBaseUrl}${path}".httpPost()
                .header(Headers.ACCEPT to CONTENTTYPE_JSON_UTF8)
                .header(Headers.ACCEPT_ENCODING to ENCODING_GZIP_DEFLATE)
                .timeout(ONE_MINUTE_IN_MILISECONDS)
        if (body.isNotEmpty()) {
            request.body(body)
            request.headers[Headers.CONTENT_TYPE] = CONTENTTYPE_JSON_UTF8
        }
        authorizer.authorize(request)
        val (_, response, result) = request.response()
        handleFailure(response, result)
        return result.get()
    }
}

interface AuthorizationMethod{
    fun authorize(request: Request)
    fun initialize(jsonHttpClient: JsonHttpClient)
}

object NullAuthorizer : AuthorizationMethod {
    override fun initialize(jsonHttpClient: JsonHttpClient) { /*do nothing*/ }
    override fun authorize(request: Request) { /*do nothing*/ }
}

class UsernamePasswordAuthorization(val username : String, val password : String) : AuthorizationMethod {

    companion object {
        private val cookieRegex: Regex = """(.+)="(.+)".+""".toRegex()
    }

    private lateinit var jsonHttpClient: JsonHttpClient
    val loginCookie: Pair<String, String> by lazy {
        JsonHttpClient.Headers.COOKIE to login()
    }

    override fun initialize(jsonHttpClient: JsonHttpClient) {
        this.jsonHttpClient = jsonHttpClient
    }

    override fun authorize(request: Request) {
        request.header(loginCookie)
    }

    fun login(): String {
        val (_, response, result) = "${jsonHttpClient.hostBaseUrl}/authenticate"
                .httpPost(listOf("username" to username, "password" to password))
                .response()
        jsonHttpClient.handleFailure(response, result)
        return extractCookie(response)
    }

    private fun extractCookie(response: Response): String {
        val cookies = response.headers[JsonHttpClient.Headers.SET_COOKIE].map { cookie ->
            cookieRegex.matchEntire(cookie)!!.destructured
        }
        return cookies.map { (name, value) -> "$name=\"$value\"" }.joinToString(separator = ";")
    }
}

class BearerAuthorization (val bearer:String) : AuthorizationMethod {

    override fun initialize(jsonHttpClient: JsonHttpClient) {}

    override fun authorize(request: Request) {
        request.header(Headers.AUTHORIZATION, "Bearer $bearer")
    }
}

class UnexpectedServerError(message: String) : Throwable(message)

class ServerUnreachable(url: String) : Throwable(url)

class HttpRequestFailed(message: String) : Throwable(message)