import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QianFanTest {
    @Test
    fun generationTest() {
        val model = QianfanApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
            secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
            model = "ernie-4.0-8k-latest",
        ).asChatModel {
            systemInstruction("You are a lovely cat, you should act as if you are a cat.")
        }

        val response = runBlocking {
            model.chat("hello")
        }
        println(response)
    }
}