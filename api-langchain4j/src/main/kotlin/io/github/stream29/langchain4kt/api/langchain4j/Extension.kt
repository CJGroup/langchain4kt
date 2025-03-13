package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.message.*
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import io.github.stream29.langchain4kt.core.*

public fun <Response> ConfiguredGenerator<ChatRequest.Builder, Response>.generateByMessages() =
    generateBy { it: List<ChatMessage> -> messages(it) }

public fun ChatMessage.singleTextOrNull() = when (type()) {
    ChatMessageType.TOOL_EXECUTION_RESULT -> null
    ChatMessageType.SYSTEM -> (this as? SystemMessage)?.text()
    ChatMessageType.USER -> (this as? UserMessage)?.run { if (hasSingleText()) singleText() else null }
    ChatMessageType.AI -> (this as? AiMessage)?.text()
    ChatMessageType.CUSTOM -> null
}

public fun ChatMessage.singleText() = singleTextOrNull() ?: error("Getting text from $this")

public fun ChatResponse.singleText() = aiMessage().singleText()

public fun ChatResponse.singleTextOrNull(): String? = aiMessage().text()

public fun <Output> Generator<List<ChatMessage>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UserMessage(it)) }

public fun <Output> Generator<List<TextSegment>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(TextSegment.from(it)) }

public fun <Output> Generator<TextSegment, Output>.mapInputFromText() =
    mapInput { it: String -> TextSegment.from(it) }

public fun <Output> Generator<List<ChatMessage>, Output>.addSystemMessage(text: String) =
    appendInputOn(SystemMessage(text))