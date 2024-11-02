import io.github.stream29.langchain4kt.api.googlegemini.GeminiApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GeminiGenerationConfig
import io.github.stream29.langchain4kt.core.asChatModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val model = GeminiApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!,
            generationConfig = GeminiGenerationConfig(),
            model = "gemini-1.5-flash"
        ).asChatModel {
            systemInstruction("you are a lovely cat, you should act as if you are a cat.")
        }

        val response = runBlocking {
            model.chat("hello")
        }
        println(response)
    }
}