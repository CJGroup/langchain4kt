import dev.langchain4j.community.model.dashscope.QwenChatModel
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.segment.TextSegment
import io.github.stream29.langchain4kt.api.langchain4j.asGenerator
import io.github.stream29.langchain4kt.core.generateBy
import io.github.stream29.langchain4kt.core.mapInput
import io.github.stream29.langchain4kt.core.mapOutput
import io.github.stream29.union.consume0
import io.github.stream29.union.consume1
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

val apiKey = System.getenv("ALIBABA_QWEN_API_KEY")
    ?: throw RuntimeException("ALIBABA_QWEN_API_KEY is not set")


class Langchain4jQwenTest {
    @Test
    fun `normal generation`() {
        val generate =
            QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
                .asGenerator()
                .generateBy { it: List<ChatMessage> -> messages(it) }
                .mapInput { it: String -> listOf(UserMessage(it)) }
                .mapOutput { it.aiMessage().text() }
        val response = runBlocking {
            generate("hello, can you dance for me?")
        }
        println(response)
    }

    @Test
    fun `stream generation`() {
        val generate =
            QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
                .asGenerator()
                .generateBy { it: List<ChatMessage> -> messages(it) }
                .mapInput { it: String -> listOf(UserMessage(it)) }
        runBlocking {
            generate("hello, who are you?").collect {
                it.consume0 { print(it.text()); System.out.flush() }
                    .consume1 { println(); println(it) }
            }
        }
    }

    @Test
    fun `embed and compare`() {
        operator fun FloatArray.times(other: FloatArray): Double {
            return this.zip(other).sumOf { (it.first * it.second).toDouble() }
        }

        val embed =
            QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-v3")
                .build()
                .asGenerator()
                .mapInput { it: String -> listOf(TextSegment.from(it)) }
                .mapOutput { it.content().first().vector() }
        runBlocking {
            val embedding1 = embed("hello? Is there anyone?")
            val embedding2 = embed("Excuse me, anybody here?")
            val embedding3 = embed("Let's go")
            println("embedding1 * embedding2 = ${embedding1 * embedding2}")
            println("embedding1 * embedding3 = ${embedding1 * embedding3}")
            assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
        }
    }
}