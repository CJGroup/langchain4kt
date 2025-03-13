package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.embedding.EmbeddingRequestBuilder
import io.github.stream29.langchain4kt.core.*

public fun <Response> ConfiguredGenerator<ChatCompletionRequestBuilder, Response>.generateByMessages() =
    generateBy(ChatCompletionRequestBuilder::messages)

public fun <Response> ConfiguredGenerator<EmbeddingRequestBuilder, Response>.generateByInput() =
    generateBy(EmbeddingRequestBuilder::input)

public fun ChatMessage.textOrNull() = when (val content = messageContent) {
    is TextContent -> content.content
    else -> null
}

public fun ChatMessage.text() = textOrNull() ?: error("Getting text from $this")

public fun ChatCompletion.singleText() = choices.first().message.content ?: error("No text in $this")

public fun ChatCompletion.singleTextOrNull() = choices.first().message.textOrNull()

public fun ChatCompletionChunk.singleText() = singleTextOrNull() ?: error("No text in $this")

public fun ChatCompletionChunk.singleTextOrNull() = choices.first().delta?.content

public fun <Output> Generator<List<ChatMessage>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(ChatMessage.User(it)) }

public fun <Output> Generator<List<ChatMessage>, Output>.addSystemMessage(text: String) =
    appendInputOn(ChatMessage.System(text))