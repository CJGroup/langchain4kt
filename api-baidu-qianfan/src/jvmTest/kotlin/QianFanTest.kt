import chat.SimpleChatLanguageModel
import chat.input.Context
import chat.message.MessageSender
import chat.message.TextMessage
import chat.output.Response
import io.github.stream29.streamlin.prettyPrintln
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QianFanTest {
    @Test
    fun generationTest() {
        val context = Context(
            systemInstruction = TextMessage(
                MessageSender.System,
                "You are a lovely cat, you should act as if you are a cat."
            )
        )

        val apiProvider = QianfanApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
            secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
            model = "ernie-4.0-8k-latest"
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