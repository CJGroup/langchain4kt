package io.github.stream29.langchain4kt2.api.springai

import io.github.stream29.langchain4kt2.core.ConfigurableGenerator
import io.github.stream29.langchain4kt2.core.Generator
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import kotlin.coroutines.resume

public class PromptBuilder {
    public lateinit var messages: List<Message>
    public var chatOptions: ChatOptions? = null
}

public fun ChatModel.asGenerator() =
    ConfigurableGenerator(::PromptBuilder) { call(Prompt(it.messages, it.chatOptions)) }

public fun ChatModel.asStreamingGenerator() =
    ConfigurableGenerator(::PromptBuilder) { stream(Prompt(it.messages, it.chatOptions)).asFlow() }

public fun EmbeddingModel.asEmbeddingGenerator(): Generator<List<String>, EmbeddingResponse> =
    { list -> suspendCancellableCoroutine { continuation -> continuation.resume(embedForResponse(list)) } }