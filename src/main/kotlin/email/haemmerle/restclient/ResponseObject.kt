package email.haemmerle.restclient

data class ResponseObject(val success: Boolean, val code: Number = 0, val error: String = "", val errors: Error = Error("", "")) {
    data class Error(val reasonKey: String, val reason: String)

    fun errorText(): String {
        if (error.isNotEmpty()) {
            return error
        }
        if (errors.reason.isNotEmpty()) {
            return "${errors.reasonKey} - ${errors.reason}"
        }
        return ""
    }
}