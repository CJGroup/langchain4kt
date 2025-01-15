import dev.langchain4j.model.dashscope.QwenChatModel
import dev.langchain4j.model.dashscope.QwenEmbeddingModel
import dev.langchain4j.model.dashscope.QwenStreamingChatModel
import io.github.stream29.langchain4kt.api.langchain4j.Langchain4jChatApiProvider
import io.github.stream29.langchain4kt.api.langchain4j.Langchain4jEmbeddingApiProvider
import io.github.stream29.langchain4kt.api.langchain4j.Langchain4jStreamChatApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import io.github.stream29.langchain4kt.streaming.asStreamChatModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

val apiKey = System.getenv("ALIBABA_QWEN_API_KEY")
    ?: throw RuntimeException("ALIBABA_QWEN_API_KEY is not set")


class Langchain4jQwenTest {
    @Test
    fun `normal generation`() {
        val langchain4jModel =
            QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
        val langchain4ktModel = Langchain4jChatApiProvider(langchain4jModel).asChatModel {
            systemInstruction("you are a lovely cat, you should act like a lovely cat.")
        }
        val response = runBlocking {
            langchain4ktModel.chat("hello")
        }
        println(response)
    }

    @Test
    fun `stream generation`() {
        val langchain4jModel =
            QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
        val model = Langchain4jStreamChatApiProvider(langchain4jModel).asStreamChatModel {
            systemInstruction("you are a lovely cat, you should act like a lovely cat.")
        }
        runBlocking {
            model.chat("hello, can you dance for me?").collect {
                print(it)
                System.out.flush()
                delay(100)
            }
            println()
            model.chat("thank you").collect {
                print(it)
                System.out.flush()
                delay(100)
            }
        }
    }

    @Test
    fun `stream generation with rollback and queueing`() {
        val langchain4jModel =
            QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
        val model = Langchain4jStreamChatApiProvider(langchain4jModel).asStreamChatModel {
            systemInstruction("you are a lovely cat, you should act like a lovely cat.")
        }
        runBlocking {
            assertFails {
                model.chat("hello, can you dance for me?").collect {
                    throw RuntimeException("test")
                }
            }
            model.chat("hello").collect {
                print(it)
                System.out.flush()
                delay(100)
            }
            assertEquals(2, model.context.history.size)
        }
    }

    @Test
    fun `embed and compare`() {
        operator fun FloatArray.times(other: FloatArray): Double {
            return this.zip(other).sumOf { (it.first * it.second).toDouble() }
        }

        val langchain4jModel = QwenEmbeddingModel.builder().apiKey(apiKey).modelName("text-embedding-v3").build()
        val langchain4ktApiProvider = Langchain4jEmbeddingApiProvider(langchain4jModel)
        runBlocking {
            val embedding1 = langchain4ktApiProvider.embed("hello? Is there anyone?")
            val embedding2 = langchain4ktApiProvider.embed("Excuse me, anybody here?")
            val embedding3 = langchain4ktApiProvider.embed("Let's go")
            println("embedding1 * embedding2 = ${embedding1 * embedding2}")
            println("embedding1 * embedding3 = ${embedding1 * embedding3}")
            assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
        }
    }
}