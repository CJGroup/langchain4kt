import chat.IChatApiProvider
import chat.input.IContext
import chat.input.getSafeAs
import chat.message.BuiltinMessageSender
import chat.message.TextMessage
import chat.output.Response
import io.github.stream29.streamlin.serialize.transform.Transformer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class GeminiApiProvider(
    val httpClient: HttpClient,
    val apiKey: String
) : IChatApiProvider {
    override fun generate(context: IContext): Response =
        runBlocking {
            httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/") {
                url {
                    appendPathSegments("${context.config.getSafeAs<String>("model")!!}:generateContent")
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
                        generationConfig = Transformer.transform(context.config),
                        systemInstruction = context.systemInstruction?.let {
                            GeminiContent(
                                listOf(mutableMapOf("text" to it.content.toString())),
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
                .let { Response.of(TextMessage(BuiltinMessageSender.Model,it)) }
        }
}