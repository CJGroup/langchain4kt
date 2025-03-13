import dev.langchain4j.community.model.dashscope.QwenChatModel
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel
import io.github.stream29.langchain4kt.api.langchain4j.asGenerator
import io.github.stream29.langchain4kt.api.langchain4j.generateByMessages
import io.github.stream29.langchain4kt.api.langchain4j.mapInputFromText
import io.github.stream29.langchain4kt.api.langchain4j.singleText
import io.github.stream29.langchain4kt.core.mapOutput
import io.github.stream29.langchain4kt.core.mapSingle
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
                .generateByMessages()
                .mapInputFromText()
                .mapOutput { it.singleText() }
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
                .generateByMessages()
                .mapInputFromText()
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
                .mapOutput { it.content() }
                .mapSingle()
                .mapInputFromText()
                .mapOutput { it.vector() }
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