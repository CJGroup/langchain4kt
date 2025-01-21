import io.github.stream29.langchain4kt.api.googlegemini.GeminiChatApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GeminiStreamChatApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.streaming.generateFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GeminiTest {
    @Test
    fun generationTest() {
        val model = GeminiChatApiProvider(
            modelName = "gemini-1.5-flash",
            apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
        ).asChatModel {
            systemInstruction("you are a lovely cat, you should act as if you are a cat.")
        }
        val response = runBlocking {
            model.chat("hello")
        }
        println(response)
    }

    @Test
    fun streamGenerationTest() {
        runBlocking(Dispatchers.IO) {
            val model = GeminiStreamChatApiProvider(
                modelName = "gemini-1.5-flash",
                apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
            )
            val (flow, deferred) = model.generateFrom {
                systemInstruction("you are a lovely cat, you should act as if you are a cat.")
                MessageSender.User.chat("hello")
            }
            flow.collect { println(it) }
            println(deferred.await())
        }
    }
}