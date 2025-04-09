package io.github.stream29.langchain4kt2.api.langchain4j

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.*
import io.github.stream29.langchain4kt2.core.ExperimentalLangchain4ktApi
import io.github.stream29.langchain4kt2.core.ModelTextMessage
import io.github.stream29.langchain4kt2.core.SystemTextMessage
import io.github.stream29.langchain4kt2.core.UserTextMessage
import io.github.stream29.union.Union2
import io.github.stream29.union.Union5
import io.github.stream29.union.UnsafeUnion

public typealias Langchain4jHistoryUnion = Union5<
        SystemMessage,
        UserMessage,
        AiMessage,
        ToolExecutionResultMessage,
        CustomMessage
        >

@ExperimentalLangchain4ktApi
public typealias Langchain4jHistoryLangchain4ktUnion = Union5<
        SystemMessage,
        UserTextMessage,
        ModelTextMessage,
        Langchain4jToolCallRequestListMessage,
        Langchain4jToolCallResultMessage
        >

@ExperimentalLangchain4ktApi
public typealias Langchain4jOutputLangchain4ktUnion = Union2<
        ModelTextMessage,
        Langchain4jToolCallRequestListMessage,
        >

public fun ChatMessage.asUnion(): Langchain4jHistoryUnion = UnsafeUnion(this)

public fun Langchain4jHistoryUnion.asChatMessage(): ChatMessage = value as ChatMessage

@ExperimentalLangchain4ktApi
public fun AiMessage.asLangchain4ktUnion(): Langchain4jOutputLangchain4ktUnion =
    UnsafeUnion(
        when {
            text() != null -> ModelTextMessage(text()!!)
            hasToolExecutionRequests() ->
                Langchain4jToolCallRequestListMessage(
                    toolExecutionRequests().map {
                        Langchain4jToolCallRequest(
                            it.id(),
                            it.name(),
                            it.arguments()
                        )
                    }
                )

            else -> error("Unprocessed AiMessage $this")
        }
    )

@ExperimentalLangchain4ktApi
public fun Langchain4jOutputLangchain4ktUnion.asAiMessage(): AiMessage =
    when (val value = value) {
        is ModelTextMessage -> AiMessage(value.text)
        is Langchain4jToolCallRequestListMessage -> AiMessage(
            value.list.map {
                ToolExecutionRequest.builder()
                    .id(it.id)
                    .name(it.toolId)
                    .arguments(it.param)
                    .build()
            }
        )

        else -> error("Illegal union value $value")
    }


@ExperimentalLangchain4ktApi
public fun Langchain4jHistoryUnion.asLangchain4ktUnion(): Langchain4jHistoryLangchain4ktUnion =
    UnsafeUnion(
        when (val value = value) {
            is SystemMessage -> SystemTextMessage(value.text()!!)
            is UserMessage -> UserTextMessage(value.singleText())
            is AiMessage -> value.asLangchain4ktUnion().value
            is ToolExecutionResultMessage -> Langchain4jToolCallResultMessage(
                value.id(),
                value.toolName(),
                value.text()
            )

            is CustomMessage -> error("Unprocessed CustomMessage $this")
            else -> error("Unprocessed Message $this")
        }
    )

@ExperimentalLangchain4ktApi
public fun Langchain4jHistoryLangchain4ktUnion.asLangchain4jUnion(): Langchain4jHistoryUnion =
    UnsafeUnion(
        when (val value = value) {
            is SystemTextMessage -> SystemMessage(value.text)
            is UserTextMessage -> UserMessage(value.text)
            is ModelTextMessage -> AiMessage(value.text)
            is Langchain4jToolCallRequestListMessage -> AiMessage(
                value.list.map {
                    ToolExecutionRequest.builder()
                        .id(it.id)
                        .name(it.toolId)
                        .arguments(it.param)
                        .build()
                }
            )

            is Langchain4jToolCallResultMessage -> ToolExecutionResultMessage(value.id, value.toolId, value.result)
            else -> error("Illegal union value $value")
        }
    )