import io.github.stream29.langchain4kt.core.generateFrom
import io.github.stream29.langchain4kt.streaming.generateFrom
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class QianfanTest {
    @Test
    fun `direct call`() {
        val response = qianFanChatModel.call("hello")
        println(response)
    }

    @Test
    fun `text response`() = runBlocking {
        val response = chatApiProvider.generateFrom("hello")
        println(response)
    }

    @Test
    fun `streaming response`() = runBlocking {
        val response = streamChatApiProvider.generateFrom("hello")
        response.collect {
            println("collected: $it")
        }
    }

    @Test
    fun `embedding compare`() = runBlocking {
        operator fun FloatArray.times(other: FloatArray) = this.zip(other).sumOf {
            (it.first * it.second).toDouble()
        }

        val embedding1 = embeddingApiProvider.embed("hello? Is there anyone?")
        val embedding2 = embeddingApiProvider.embed("Excuse me, anybody here?")
        val embedding3 = embeddingApiProvider.embed("Let's go")
        println("embedding1 * embedding2 = ${embedding1 * embedding2}")
        println("embedding1 * embedding3 = ${embedding1 * embedding3}")
        assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
    }
}