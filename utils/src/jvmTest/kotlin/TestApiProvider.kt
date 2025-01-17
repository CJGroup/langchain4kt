import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

class TestApiProvider(
    val index: Int
) : ChatApiProvider<Nothing?> {
    override suspend fun generate(context: Context): Response<Nothing?> {
        println("TestApiProvider $index generate with context: $context")
        return Response("", null)
    }
}