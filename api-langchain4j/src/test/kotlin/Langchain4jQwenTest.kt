import dev.langchain4j.community.model.dashscope.QwenChatModel
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.segment.TextSegment
import io.github.stream29.langchain4kt.api.langchain4j.toApiProvider
import io.github.stream29.langchain4kt.api.langchain4j.toGenerator
import io.github.stream29.langchain4kt.api.langchain4j.toStreamingApiProvider
import io.github.stream29.union.SafeUnion5
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
                .toApiProvider()
        val response = runBlocking {
            generate(listOf(SafeUnion5(UserMessage("hello, can you dance for me?"))))
        }
        println(response.content.text())
    }

    @Test
    fun `stream generation`() {
        val generate =
            QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .build()
                .toStreamingApiProvider()
        runBlocking {
            generate(listOf(SafeUnion5(UserMessage("hello, who are you?")))).collect {
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
                .toGenerator()
        runBlocking {
            val embedding1 = embed(listOf(TextSegment.from("hello? Is there anyone?"))).content.first().vector()
            val embedding2 = embed(listOf(TextSegment.from("Excuse me, anybody here?"))).content.first().vector()
            val embedding3 = embed(listOf(TextSegment.from("Let's go"))).content.first().vector()
            println("embedding1 * embedding2 = ${embedding1 * embedding2}")
            println("embedding1 * embedding3 = ${embedding1 * embedding3}")
            assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
        }
    }
}