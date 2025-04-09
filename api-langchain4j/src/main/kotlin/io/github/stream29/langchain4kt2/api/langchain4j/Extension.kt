package io.github.stream29.langchain4kt2.api.langchain4j

import dev.langchain4j.data.message.*
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import io.github.stream29.langchain4kt2.core.ConfiguredGenerator
import io.github.stream29.langchain4kt2.core.ExperimentalLangchain4ktApi
import io.github.stream29.langchain4kt2.core.Generator
import io.github.stream29.langchain4kt2.core.SystemTextMessage
import io.github.stream29.langchain4kt2.core.UserTextMessage
import io.github.stream29.langchain4kt2.core.appendInputOn
import io.github.stream29.langchain4kt2.core.generateBy
import io.github.stream29.langchain4kt2.core.mapInput
import io.github.stream29.langchain4kt2.core.mapInputHistory
import io.github.stream29.langchain4kt2.core.mapOutput
import io.github.stream29.union.UnsafeUnion

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

@JvmName("mapInputFromText_List_ChatMessage")
public fun <Output> Generator<List<ChatMessage>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UserMessage(it)) }

@JvmName("mapInputFromText_List_Langchain4jHistoryUnion")
public fun <Output> Generator<List<Langchain4jHistoryUnion>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UnsafeUnion(UserMessage(it))) }

@ExperimentalLangchain4ktApi
@JvmName("mapInputFromText_List_Langchain4jHistoryLangchain4ktUnion")
public fun <Output> Generator<List<Langchain4jHistoryLangchain4ktUnion>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UnsafeUnion(UserTextMessage(it))) }

@JvmName("mapInputFromText_List_TextSegment")
public fun <Output> Generator<List<TextSegment>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(TextSegment.from(it)) }

@JvmName("mapInputFromText_TextSegment")
public fun <Output> Generator<TextSegment, Output>.mapInputFromText() =
    mapInput { it: String -> TextSegment.from(it) }

public fun <Output> Generator<ChatMessage, Output>.mapInputFromUnion() =
    mapInput { it: Langchain4jHistoryUnion -> it.asChatMessage() }

@ExperimentalLangchain4ktApi
public fun <Output> Generator<Langchain4jHistoryUnion, Output>.mapInputFromLangchain4ktUnion() =
    mapInput { it: Langchain4jHistoryLangchain4ktUnion -> it.asLangchain4jUnion() }

@JvmName("mapInputFromMessage_List_ChatMessage")
public fun <Output> Generator<List<ChatMessage>, Output>.addSystemMessage(text: String) =
    appendInputOn(SystemMessage(text))

@JvmName("mapInputFromMessage_List_Langchain4jHistoryUnion")
public fun <Output> Generator<List<Langchain4jHistoryUnion>, Output>.addSystemMessage(text: String) =
    appendInputOn(UnsafeUnion(SystemTextMessage(text)))

public fun Generator<List<ChatMessage>, AiMessage>.mapLangchain4jUnion() =
    mapInputHistory { it: Langchain4jHistoryUnion -> it.asChatMessage() }

@ExperimentalLangchain4ktApi
public fun Generator<List<Langchain4jHistoryUnion>, AiMessage>.mapLangchain4ktUnion() =
    mapInputHistory { it: Langchain4jHistoryLangchain4ktUnion -> it.asLangchain4jUnion() }
        .mapOutput { it.asLangchain4ktUnion() }