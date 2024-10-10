import io.ktor.client.request.*
import io.ktor.http.*

const val urlString = "https://generativelanguage.googleapis.com/"

val key = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!

fun HttpRequestBuilder.function(name: String) {
    url {
        appendPathSegments("v1beta", "models")
        appendPathSegments(name)
        parameters.append("key", key)
    }
    contentType(ContentType.Application.Json)
}

fun HttpRequestBuilder.generateContent(model: String) {
    method = HttpMethod.Post
    function("$model:generateContent")
}