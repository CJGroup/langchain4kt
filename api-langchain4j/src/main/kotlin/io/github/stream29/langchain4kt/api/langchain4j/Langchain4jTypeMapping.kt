package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.CustomMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import io.github.stream29.union.Union5

public typealias Langchain4jHistoryType = Union5<
        SystemMessage,
        UserMessage,
        AiMessage,
        ToolExecutionResultMessage,
        CustomMessage
        >