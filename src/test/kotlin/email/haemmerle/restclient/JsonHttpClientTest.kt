package email.haemmerle.httpclient

import email.haemmerle.restclient.BearerAuthorization
import email.haemmerle.restclient.JsonHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JsonHttpClientTest {

    var mockServer = MockWebServer()

    @BeforeEach
    fun beforeEach(){
        mockServer = MockWebServer()
        mockServer.start()
    }

    @AfterEach
    fun afterEach() {
        mockServer.shutdown()
    }

    @Test
    fun canRequestWithBearerAuthorization() {
        // prepare
        mockServer.enqueue(MockResponse().setResponseCode(200))
        val sut = JsonHttpClient("http://${mockServer.hostName}:${mockServer.port}/", BearerAuthorization("test"))

        // when
        sut.performJsonGetRequest("/")

        // then
        val request = mockServer.takeRequest()
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer test")
    }

    @Test
    fun canReturnObjectFromGetRequest() {
        // prepare
        mockServer.enqueue(MockResponse().setResponseCode(200).setBody("""{ "name": "test" }"""))
        val sut = JsonHttpClient("http://${mockServer.hostName}:${mockServer.port}/")

        // when
        val result = sut.performJsonGetRequest<TestResultObject>("/")

        // then
        assertThat(result!!.name).isEqualTo("test")
    }

    @Test
    fun canReturnObjectFromPostRequest() {
        // prepare
        mockServer.enqueue(MockResponse().setResponseCode(200).setBody("""{ "name": "test" }"""))
        val sut = JsonHttpClient("http://${mockServer.hostName}:${mockServer.port}/")

        // when
        val result = sut.performJsonGetRequest<TestResultObject>("/")

        // then
        assertThat(result!!.name).isEqualTo("test")
    }

    class TestResultObject(val name: String)

}