import chat.ChatLanguageModel
import chat.IChatApiProvider
import chat.input.ChatConfig
import chat.input.Context
import chat.input.IContext
import chat.input.get
import chat.message.BuiltinMessageSender
import chat.message.BuiltinMessageType
import chat.message.TextMessage
import chat.output.Response
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiApiProvider : IChatApiProvider {
    override fun generate(context: IContext): Response =
        runBlocking {
            httpClient.request(urlString) {
                generateContent(context.config.get<String>("model")!!)
                setBody(
                    GeminiRequest(
                        contents = context.history.asSequence()
                            .filter { it.sender != BuiltinMessageSender.System }
                            .map {
                                GeminiContent(
                                    listOf(mutableMapOf("text" to it.content.toString())),
                                    it.sender.toString()
                                )
                            }.toMutableList(),
                        generationConfig = GenerationConfig(
                            temperature = 1.1
                        ),
                        systemInstruction = GeminiContent(
                            listOf(mutableMapOf("text" to "You are doing great!")),
                            "user")
                    )
                )
            }.body<GeminiResponse>().candidates.let { it.first().content }.let { TestResponse(it.text) }
        }

}

data class TestResponse(override val content: String) : Response {
    override val type = BuiltinMessageType.Text
    override val sender = BuiltinMessageSender.Model
}

class GeminiTest {
    @Test
    fun generationTest() {
        val apiProvider = GeminiApiProvider()
        val config = ChatConfig(mapOf(
            "model" to "gemini-1.5-flash"
        ))
        val model = ChatLanguageModel(Context(config = config), apiProvider)
        model.chat(TextMessage(BuiltinMessageSender.User,"hello"))
    }
}