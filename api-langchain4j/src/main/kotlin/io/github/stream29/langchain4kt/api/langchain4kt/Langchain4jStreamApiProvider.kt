package io.github.stream29.langchain4kt.api.langchain4kt

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.StreamingResponseHandler
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.output.Response
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking

public data class Langchain4jStreamApiProvider(
    val model: StreamingChatLanguageModel,
): StreamChatApiProvider<Langchain4jMetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<Langchain4jMetaInfo> {
        val langchain4jContext = buildList {
            context.systemInstruction?.let {
                add(SystemMessage(it))
            }
            context.history.forEach {
                when (it.sender) {
                    MessageSender.User -> add(UserMessage(it.content))
                    MessageSender.Model -> add(AiMessage(it.content))
                }
            }
        }
        val tokenChannel = Channel<String>()
        val metaInfo = CompletableDeferred<Langchain4jMetaInfo>()
        val streamingResponseHandler = object : StreamingResponseHandler<AiMessage> {
            override fun onNext(token: String) {
                runBlocking {
                    tokenChannel.send(token)
                }
            }

            override fun onError(error: Throwable?) {
                tokenChannel.close(error)
            }

            override fun onComplete(response: Response<AiMessage>) {
                tokenChannel.close()
                metaInfo.complete(Langchain4jMetaInfo(response))
            }
        }
        model.generate(langchain4jContext, streamingResponseHandler)
        return StreamResponse(
            message = tokenChannel.consumeAsFlow(),
            metaInfo = metaInfo
        )
    }
}