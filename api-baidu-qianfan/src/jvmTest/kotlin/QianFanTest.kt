import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.core.SimpleChatLanguageModel
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.message.TextMessage
import io.github.stream29.langchain4kt.core.output.Response
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