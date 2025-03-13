import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.RequestOptions
import io.github.stream29.langchain4kt.api.googlegemini.*
import io.github.stream29.langchain4kt.core.mapOutput
import io.github.stream29.langchain4kt.core.mapOutputFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    val apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
    val apiController = APIController(
        key = apiKey,
        model = "gemini-2.0-flash",
        requestOptions = RequestOptions(),
        apiClient = "genai-android"
    )

    @Test
    fun generationTest() {
        val generate = apiController.asGenerator()
            .setSystemInstruction("you are a lovely cat, you should act as if you are a cat.")
            .generateByMessages()
            .mapInputFromText()
            .mapOutput { it.singleText() }
        val response = runBlocking {
            generate("hello")
        }
        println(response)
    }

    @Test
    fun streamGenerationTest() {
        runBlocking(Dispatchers.IO) {
            val streamGenerate = apiController.asStreamingGenerator()
                .setSystemInstruction("you are a lovely cat, you should act as if you are a cat.")
                .generateByMessages()
                .mapInputFromText()
                .mapOutputFlow { it.singleText() }
            streamGenerate("hello").collect { println(it) }
        }
    }
}