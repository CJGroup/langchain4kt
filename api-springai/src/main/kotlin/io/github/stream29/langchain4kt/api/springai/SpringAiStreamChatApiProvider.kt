package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.ai.chat.prompt.Prompt

/**
 * Wrapping [StreamingChatModel] to [StreamChatApiProvider].
 */
public data class SpringAiStreamChatApiProvider(
    val streamModel: StreamingChatModel
) : StreamChatApiProvider<List<ChatResponse>> {
    override suspend fun generate(context: Context): StreamResponse<List<ChatResponse>> {
        val prompt = Prompt(fromContext(context))
        val output = Channel<String>()
        val metaInfo = Channel<ChatResponse>()
        streamModel.stream(prompt).subscribe(
            {
                runBlocking {
                    output.send(it.result.output.content)
                    metaInfo.send(it)
                }
            },
            {
                output.close(it)
                metaInfo.close(it)
            },
            {
                output.close()
                metaInfo.close()
            }
        )
        // Use CoroutineScope(Dispatchers.IO) to avoid blocking the current thread
        return StreamResponse(
            message = output.consumeAsFlow(),
            metaInfo = CoroutineScope(Dispatchers.IO).async { metaInfo.consumeAsFlow().toList() }
        )
    }
}

/**
 * Wrapping [StreamingChatModel] to [StreamChatApiProvider].
 */
public fun StreamingChatModel.asLangchain4ktProvider(): SpringAiStreamChatApiProvider =
    SpringAiStreamChatApiProvider(this)