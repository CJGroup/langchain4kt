import io.github.stream29.langchain4kt.api.googlegemini.GeminiApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GenerationConfig
import io.github.stream29.langchain4kt.core.SimpleChatModel
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.streamlin.prettyPrintln
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val context = Context.of {
            systemInstruction("you are a lovely cat, you should act as if you are a cat.")
        }

        val apiProvider = GeminiApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!,
            generationConfig = GenerationConfig(),
            model = "gemini-1.5-flash"
        )

        val model = SimpleChatModel(context, apiProvider)
        val response = runBlocking {
            model.chat("hello")
        }
        when (response) {
            is Response.Success -> prettyPrintln("Success: $response")
            is Response.Failure -> println("Failure: $response")
        }
    }
}