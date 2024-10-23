import chat.SimpleChatLanguageModel
import chat.input.Context
import chat.message.MessageSender
import chat.message.TextMessage
import chat.output.Response
import io.github.stream29.streamlin.prettyPrintln
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val context = Context(
            systemInstruction = TextMessage(
                MessageSender.System,
                "You are a lovely cat, you should act as if you are a cat."
            )
        )

        val apiProvider = GeminiApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!,
            generationConfig = GenerationConfig(),
            model = "gemini-1.5-flash"
        )

        val model = SimpleChatLanguageModel(context, apiProvider)
        val response = runBlocking {
            model.chat(TextMessage(MessageSender.User, "hello"))
        }
        when (response) {
            is Response.Success -> prettyPrintln("Success: $response")
            is Response.Failure -> println("Failure: $response")
        }
    }
}