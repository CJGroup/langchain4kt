package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.ChatResponseMetadata
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import io.github.stream29.langchain4kt.core.StreamingApiProvider
import io.github.stream29.union.Union2
import io.github.stream29.union.UnsafeUnion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking

public fun StreamingChatLanguageModel.toStreamingApiProvider(): StreamingApiProvider<Langchain4jHistoryType, Union2<AiMessage, ChatResponseMetadata>> =
    { history ->
        val channel = Channel<Union2<AiMessage, ChatResponseMetadata>>()
        val handler = object : StreamingChatResponseHandler {
            override fun onPartialResponse(partialResponse: String?) {
                runBlocking {
                    partialResponse?.let { runBlocking { channel.send(UnsafeUnion(it)) } }
                }
            }
            override fun onCompleteResponse(completeResponse: ChatResponse?) {
                runBlocking {
                    completeResponse?.run {
                        channel.send(UnsafeUnion(aiMessage()!!))
                        channel.send(UnsafeUnion(metadata()!!))
                    }
                    channel.close()
                }
            }
            override fun onError(error: Throwable?) {
                channel.close(error)
            }
        }
        chat(history.map { it.value as ChatMessage }, handler)

        channel.consumeAsFlow()
    }