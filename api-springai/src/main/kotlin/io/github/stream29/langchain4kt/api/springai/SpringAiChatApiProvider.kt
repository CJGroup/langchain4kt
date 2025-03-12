package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.ApiProvider
import io.github.stream29.union.Union4
import org.springframework.ai.chat.messages.*
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public typealias SpringAiHistoryType = Union4<
        SystemMessage,
        UserMessage,
        AssistantMessage,
        ToolResponseMessage
        >

public fun ChatModel.asApiProvider(): ApiProvider<SpringAiHistoryType, ChatResponse> =
    { history ->
        suspendCoroutine { continuation ->
            continuation.resume(call(Prompt(history.map { it.value as Message })))
        }
    }