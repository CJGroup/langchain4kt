import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class QianfanTest {
    @Test
    fun `text response`() = runBlocking {
        val response = generate("hello")
        println(response)
    }

    @Test
    fun `streaming response`() = runBlocking {
        val response = streamGenerate("hello")
        response.collect {
            println("collected: $it")
        }
    }

    @Test
    fun `embedding compare`() = runBlocking {
        operator fun FloatArray.times(other: FloatArray) = this.zip(other).sumOf {
            (it.first * it.second).toDouble()
        }

        val embedding1 = embed("hello? Is there anyone?")
        val embedding2 = embed("Excuse me, anybody here?")
        val embedding3 = embed("Let's go")
        println("embedding1 * embedding2 = ${embedding1 * embedding2}")
        println("embedding1 * embedding3 = ${embedding1 * embedding3}")
        assertTrue { embedding1 * embedding2 > embedding1 * embedding3 }
    }
}