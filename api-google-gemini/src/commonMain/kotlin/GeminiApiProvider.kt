import chat.ChatApiProvider
import chat.input.Context
import chat.message.MessageSender
import chat.message.TextMessage
import chat.output.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class GeminiApiProvider(
    val httpClient: HttpClient,
    val generationConfig: GenerationConfig,
    val model: String,
    val apiKey: String
) : ChatApiProvider<GeminiResponse, String> {
    private val json = Json {
        ignoreUnknownKeys = true
    }
    override suspend fun generate(context: Context): Response<*, GeminiResponse, String> {
        try {
            val httpResponse = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/") {
                configureRequestBy(context)
            }
            val bodyAsText = httpResponse.bodyAsText()
            try {
                val geminiResponse = json.decodeFromString<GeminiResponse>(bodyAsText)
                return Response.Success(
                    message = TextMessage(
                        MessageSender.Model,
                        geminiResponse.candidates
                            .first()
                            .content
                            .text
                    ),
                    successInfo = geminiResponse
                )
            } catch (e: Exception) {
                return Response.Failure(
                    failInfo = bodyAsText
                )
            }
        } catch (e: Exception) {
            return Response.Failure(
                e.message ?: "Unknown error"
            )
        }
    }

    private fun HttpRequestBuilder.configureRequestBy(context: Context) {
        url {
            appendPathSegments("$model:generateContent")
            parameters.append("key", apiKey)
        }
        contentType(ContentType.Application.Json)
        setBody(
            GeminiRequest(
                contents = context.history.asSequence()
                    .map {
                        GeminiContent(
                            listOf(mutableMapOf("text" to it.content.toString())),
                            it.sender.toString()
                        )
                    }.toMutableList(),
                generationConfig = generationConfig,
                systemInstruction = context.systemInstruction?.let {
                    GeminiContent(
                        listOf(mutableMapOf("text" to it.content)),
                        it.sender.toString()
                    )
                },
            )
        )
    }
}