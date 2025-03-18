import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.RequestOptions
import io.github.stream29.langchain4kt2.core.mapOutput
import io.github.stream29.langchain4kt2.core.mapOutputFlow
import io.github.stream29.langchain4kt2.api.googlegemini.asGenerator
import io.github.stream29.langchain4kt2.api.googlegemini.asStreamingGenerator
import io.github.stream29.langchain4kt2.api.googlegemini.generateByMessages
import io.github.stream29.langchain4kt2.api.googlegemini.mapInputFromText
import io.github.stream29.langchain4kt2.api.googlegemini.setSystemInstruction
import io.github.stream29.langchain4kt2.api.googlegemini.singleText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test

class GeminiTest {
    val apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
    val apiController = APIController(
        key = apiKey,
        model = "gemini-2.0-flash",
        requestOptions = RequestOptions(),
        apiClient = "genai-android"
    )

    @OptIn(ExperimentalSerializationApi::class)
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

    @OptIn(ExperimentalSerializationApi::class)
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