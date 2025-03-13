package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.union.Union4
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.ToolResponseMessage
import org.springframework.ai.chat.messages.UserMessage

public typealias SpringAiHistoryType = Union4<
        SystemMessage,
        UserMessage,
        AssistantMessage,
        ToolResponseMessage
        >