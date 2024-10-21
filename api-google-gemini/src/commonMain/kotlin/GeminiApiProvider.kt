import chat.IChatApiProvider
import chat.input.Context
import chat.input.getOrThrow
import chat.input.getSafeAs
import chat.message.MessageSender
import chat.message.TextMessage
import chat.output.Response
import io.github.stream29.streamlin.serialize.transform.Transformer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class GeminiApiProvider(
    val httpClient: HttpClient,
    val apiKey: String
) : IChatApiProvider<Unit, Unit> {
    override suspend fun generate(context: Context): Response<*, Unit, Unit> =
        httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/") {
            url {
                appendPathSegments("${context.config.getOrThrow<String>("model")}:generateContent")
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
                    generationConfig = with(Transformer) { fromMap(context.config).toSerializable() },
                    systemInstruction = context.systemInstruction?.let {
                        GeminiContent(
                            listOf(mutableMapOf("text" to it.content)),
                            it.sender.toString()
                        )
                    },
                )
            )
        }.body<GeminiResponse>()
            .candidates
            .first()
            .content
            .text
            .let { Response.Success(TextMessage(MessageSender.Model, it), Unit) }

}