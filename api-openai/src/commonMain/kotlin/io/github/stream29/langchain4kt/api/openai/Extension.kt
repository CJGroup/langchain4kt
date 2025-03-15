package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.embedding.EmbeddingRequestBuilder
import io.github.stream29.langchain4kt.core.*
import io.github.stream29.union.UnsafeUnion
import kotlin.jvm.JvmName

public fun <Response> ConfiguredGenerator<ChatCompletionRequestBuilder, Response>.generateByMessages() =
    generateByNotNullable(ChatCompletionRequestBuilder::messages)

public fun <Response> ConfiguredGenerator<EmbeddingRequestBuilder, Response>.generateByInput() =
    generateByNotNullable(EmbeddingRequestBuilder::input)

public fun ChatMessage.textOrNull() = when (val content = messageContent) {
    is TextContent -> content.content
    else -> null
}

public fun ChatMessage.text() = textOrNull() ?: error("Getting text from $this")

public fun ChatCompletion.singleMessageOrNull() = choices.firstOrNull()?.message

public fun ChatCompletion.singleMessage() = singleMessageOrNull() ?: error("No message in $this")

public fun ChatCompletion.singleText() = singleTextOrNull() ?: error("No text in $this")

public fun ChatCompletion.singleTextOrNull() = choices.firstOrNull()?.message?.textOrNull()

public fun ChatCompletionChunk.singleText() = singleTextOrNull() ?: error("No text in $this")

public fun ChatCompletionChunk.singleTextOrNull() = choices.firstOrNull()?.delta?.content

@JvmName("mapOutputToText_List_ChatMessage")
public fun <Output> Generator<List<ChatMessage>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(ChatMessage.User(it)) }

@JvmName("mapInputFromText_List_Union")
public fun <Output> Generator<List<OpenAiHistoryMessageUnion>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UnsafeUnion(UserTextMessage(it))) }

public fun <Output> Generator<List<ChatMessage>, Output>.addSystemMessage(text: String) =
    appendInputOn(ChatMessage.System(text))