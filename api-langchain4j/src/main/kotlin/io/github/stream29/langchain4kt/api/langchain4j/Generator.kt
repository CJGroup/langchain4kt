package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.ChatResponseMetadata
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.output.Response
import io.github.stream29.langchain4kt.core.ConfigurableGenerator
import io.github.stream29.langchain4kt.core.ConfiguredGenerator
import io.github.stream29.langchain4kt.core.Generator
import io.github.stream29.langchain4kt.core.Langchain4ktExperimental
import io.github.stream29.langchain4kt.core.mapInput
import io.github.stream29.langchain4kt.core.mapInputHistory
import io.github.stream29.union.Union2
import io.github.stream29.union.UnsafeUnion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


public fun ChatLanguageModel.asGenerator() = ConfigurableGenerator({ ChatRequest.builder() }) {
    suspendCancellableCoroutine { continuation -> continuation.resume(chat(it.build())) }
}

public fun StreamingChatLanguageModel.asGenerator() =
    ConfigurableGenerator({ ChatRequest.builder() }) { chatRequestBuilder ->
        val channel = Channel<Union2<AiMessage, ChatResponseMetadata>>()
        val handler = object : StreamingChatResponseHandler {
            override fun onPartialResponse(partialResponse: String) {
                runBlocking {
                    channel.send(UnsafeUnion(partialResponse))
                }
            }

            override fun onCompleteResponse(completeResponse: ChatResponse) {
                runBlocking {
                    channel.send(UnsafeUnion(completeResponse.aiMessage()!!))
                    channel.send(UnsafeUnion(completeResponse.metadata()!!))
                    channel.close()
                }
            }

            override fun onError(error: Throwable?) {
                channel.close(error)
            }
        }
        chat(chatRequestBuilder.build(), handler)
        channel.consumeAsFlow()
    }

public fun EmbeddingModel.asGenerator(): Generator<List<TextSegment>, Response<List<Embedding>>> =
    { segments -> suspendCancellableCoroutine { continuation -> continuation.resume(embedAll(segments)) } }