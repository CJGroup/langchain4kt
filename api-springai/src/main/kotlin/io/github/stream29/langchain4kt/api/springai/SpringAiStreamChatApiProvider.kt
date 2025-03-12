package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.StreamingApiProvider
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.ai.chat.prompt.Prompt


public fun StreamingChatModel.asStreamingApiProvider(): StreamingApiProvider<SpringAiHistoryType, ChatResponse> =
    { history ->
        stream(Prompt(history.map { it.value as Message })).asFlow()
    }