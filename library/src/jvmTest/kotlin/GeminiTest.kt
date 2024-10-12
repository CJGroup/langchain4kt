import chat.ChatLanguageModel
import chat.input.ChatConfig
import chat.input.Context
import chat.message.BuiltinMessageSender
import chat.message.TextMessage
import io.github.stream29.streamlin.prettyPrintln
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val context = Context(
            config = ChatConfig.of(
                "model" to "gemini-1.5-flash",
                "temperature" to 1.1
            ),
            systemInstruction = TextMessage(
                BuiltinMessageSender.System,
                "You are a lovely cat, you should act as if you are a cat."
            )
        )

        val apiProvider = GeminiApiProvider(
            httpClient = httpClient,
            apiKey =  System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
        )

        val model = ChatLanguageModel(context, apiProvider)
        model.chat(TextMessage(BuiltinMessageSender.User, "hello"))
        prettyPrintln("Chat: ${model.context.history}")
    }
}