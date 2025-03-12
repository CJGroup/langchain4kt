import io.github.stream29.union.SafeUnion4
import kotlinx.coroutines.runBlocking
import org.springframework.ai.chat.messages.UserMessage
import kotlin.test.Test
import kotlin.test.assertTrue

class SpringAiQianfanTest {
    @Test
    fun `direct call`() {
        val response = qianFanChatModel.call("hello")
        println(response)
    }

    @Test
    fun `text response`() = runBlocking {
        val response = chatApiProvider(listOf(SafeUnion4(UserMessage("hello"))))
        println(response.result.output.text)
    }

    @Test
    fun `streaming response`() = runBlocking {
        val response = streamChatApiProvider(listOf(SafeUnion4(UserMessage("hello"))))
        response.collect {
            println("collected: ${it.result.output.text}")
        }
    }

    @Test
    fun `embedding compare`() = runBlocking {
        operator fun FloatArray.times(other: FloatArray) = this.zip(other).sumOf {
            (it.first * it.second).toDouble()
        }

        val embedding1 = embeddingApiProvider("hello? Is there anyone?")
        val embedding2 = embeddingApiProvider("Excuse me, anybody here?")
        val embedding3 = embeddingApiProvider("Let's go")
        println("embedding1 * embedding2 = ${embedding1 * embedding2}")
        println("embedding1 * embedding3 = ${embedding1 * embedding3}")
        assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
    }
}