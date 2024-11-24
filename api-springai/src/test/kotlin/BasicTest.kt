import kotlin.test.Test

class BasicTest {
    @Test
    fun `direct call`() {
        val response = qianFanChatModel.call("hello")
        println(response)
    }
}