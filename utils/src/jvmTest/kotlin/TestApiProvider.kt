import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class TestApiProvider(
    val index: Int
) : ChatApiProvider<Nothing?> {
    override suspend fun generate(context: Context): Response<Nothing?> {
        println("TestApiProvider $index generate with context: $context")
        return Response("", null)
    }
}

class TestStreamChatApiProvider : StreamChatApiProvider<Nothing?> {
    override suspend fun generate(context: Context): StreamResponse<Nothing?> {
        println("TestStreamChatApiProvider generate with context: $context")
        delay(1000)
        return StreamResponse(
            flowOf("1", "2", "3").onEach { println("generating $it");delay(1000) },
            CompletableDeferred<Nothing?>().also { it.complete(null) }
        )
    }
}