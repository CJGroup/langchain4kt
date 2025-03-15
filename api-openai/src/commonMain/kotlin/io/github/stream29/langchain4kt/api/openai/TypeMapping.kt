package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import io.github.stream29.langchain4kt.core.*
import io.github.stream29.union.*
import kotlin.jvm.JvmName

public typealias OpenAiInputContentPartUnion = Union2<
        UserTextMessage,
        UserUrlImageMessage
        >

public typealias OpenAiOutputContentPartUnion = Union2<
        ModelTextMessage,
        ModelUrlImageMessage
        >

public typealias OpenAiHistoryMessageUnion = Union8<
        UserTextMessage,
        SystemTextMessage,
        ModelTextMessage,
        ListInputMessage<OpenAiInputContentPartUnion>,
        ListOutputMessage<OpenAiOutputContentPartUnion>,
        OpenAiToolCallRequest,
        OpenAiToolCallRequestListMessage,
        OpenAiToolCallResultMessage
        >

public typealias OpenAiOutputMessageUnion = Union4<
        ModelTextMessage,
        ListOutputMessage<OpenAiOutputContentPartUnion>,
        OpenAiToolCallRequest,
        OpenAiToolCallRequestListMessage
        >

public fun ContentPart.asUnionOfMessage(direction: DataDirection): Message {
    return when (this) {
        is TextPart -> TextMessage(direction, text)
        is ImagePart -> UrlImageMessage(direction, imageUrl.url)
    }
}

public fun ToolCall.asOpenAiToolCallRequestMessage(): OpenAiToolCallRequest = when (this) {
    is ToolCall.Function -> OpenAiToolCallRequest(id.id, function.name, function.arguments)
}

public fun OpenAiToolCallRequest.asUnionOfMessage() = ToolCallRequestMessage(name, param)

public fun ChatMessage.asUnionOfMessage(): OpenAiHistoryMessageUnion {
    val content = messageContent
    if (content is TextContent) {
        return UnsafeUnion(
            when (role) {
                Role.User -> UserTextMessage(content.content)
                Role.System -> SystemTextMessage(content.content)
                Role.Assistant -> ModelTextMessage(content.content)
                else -> error("Illegal role for text message: $role")
            }
        )
    }
    if (content is ListContent) {
        return UnsafeUnion(
            when (role) {
                Role.User -> ListInputMessage<OpenAiInputContentPartUnion>(content.content.map {
                    it.asUnionOfMessage(
                        DataDirection.Input
                    )
                }.map { UnsafeUnion(it) })

                Role.Assistant -> ListOutputMessage<OpenAiOutputContentPartUnion>(content.content.map {
                    it.asUnionOfMessage(
                        DataDirection.Output
                    )
                }.map { UnsafeUnion(it) })

                else -> error("Illegal role for list message: $role")
            }
        )
    }
    val nameOrNull = functionCall?.nameOrNull
    val argumentsOrNull = functionCall?.argumentsOrNull
    if (nameOrNull != null && argumentsOrNull != null) {
        return UnsafeUnion(ToolCallRequestMessage(nameOrNull, argumentsOrNull))
    }
    val toolCalls = toolCalls
    if (toolCalls != null) {
        return UnsafeUnion(ToolCallRequestListMessage(toolCalls.map {
            it.asOpenAiToolCallRequestMessage().asUnionOfMessage()
        }))
    }
    error("Unsupported message: $this")
}

@Suppress("unchecked_cast")
public fun ChatCompletion.asUnionOfMessage(): OpenAiOutputMessageUnion =
    choices.firstOrNull()
        .let { it ?: error("no legal choice in $this") }.message.asUnionOfMessage() as OpenAiOutputMessageUnion

@JvmName("asContentPart_OpenAiInputContentPartUnion")
public fun OpenAiInputContentPartUnion.asContentPart(): ContentPart {
    consume0 { return TextPart(it.text) }
    consume1 { return ImagePart(it.url) }
    error("Invalid union value: ${this.value}")
}

@JvmName("asContentPart_OpenAiOutputContentPartUnion")
public fun OpenAiOutputContentPartUnion.asContentPart(): ContentPart {
    consume0 { return TextPart(it.text) }
    consume1 { return ImagePart(it.url) }
    error("Invalid union value: ${this.value}")
}

public fun OpenAiToolCallRequest.asToolCall() =
    ToolCall.Function(
        ToolId(id),
        FunctionCall(name, param)
    )

public fun OpenAiHistoryMessageUnion.asChatMessage(): ChatMessage {
    consume0 { return ChatMessage.User(it.text) }
    consume1 { return ChatMessage.System(it.text) }
    consume2 { return ChatMessage.Assistant(it.text) }
    consume3 { return ChatMessage.User(it.list.map { it.asContentPart() }) }
    consume4 { return ChatMessage(ChatRole.Assistant, it.list.map { it.asContentPart() }) }
    consume5 { return ChatMessage.Assistant(toolCalls = listOf(it.asToolCall())) }
    consume6 { return ChatMessage.Assistant(toolCalls = it.list.map { it.asToolCall() }) }
    consume7 { return ChatMessage.Tool(it.result, ToolId(it.id)) }
    error("Invalid union value: ${this.value}")
}