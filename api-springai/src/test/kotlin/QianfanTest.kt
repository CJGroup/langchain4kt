import io.github.stream29.langchain4kt.core.generateFrom
import io.github.stream29.langchain4kt.streaming.generateFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

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
    fun `streaming response`() = runBlocking(Dispatchers.IO) {
        val response = streamChatApiProvider.generateFrom("hello")
        response.collect {
            println("collected: $it")
        }
    }
}