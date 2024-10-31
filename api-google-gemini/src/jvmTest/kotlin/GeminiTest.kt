import io.github.stream29.langchain4kt.api.googlegemini.GeminiApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GenerationConfig
import io.github.stream29.langchain4kt.core.SimpleChatModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val apiProvider = GeminiApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!,
            generationConfig = GenerationConfig(),
            model = "gemini-1.5-flash"
        )

        val model = SimpleChatModel(apiProvider) {
            systemInstruction("you are a lovely cat, you should act as if you are a cat.")
        }

        val response = runBlocking {
            model.chat("hello")
        }
        println(response)
    }
}