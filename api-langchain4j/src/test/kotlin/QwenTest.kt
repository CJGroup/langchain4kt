import dev.langchain4j.model.dashscope.QwenChatModel
import io.github.stream29.langchain4kt.api.langchain4kt.Langchain4jApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QwenTest {
    @Test
    fun generation() {
        val apiKey = System.getenv("ALIBABA_QWEN_API_KEY")
            ?: throw RuntimeException("ALIBABA_QWEN_API_KEY is not set")
        val langchain4jModel =
            QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
        val langchain4ktModel = Langchain4jApiProvider(langchain4jModel!!).asChatModel {
            systemInstruction("you are a lovely cat, you should act like a lovely cat.")
        }
        val response = runBlocking {
            langchain4ktModel.chat("hello")
        }
        println(response)
    }
}