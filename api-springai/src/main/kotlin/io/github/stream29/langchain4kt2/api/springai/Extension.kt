package io.github.stream29.langchain4kt2.api.springai

import io.github.stream29.langchain4kt2.core.ConfiguredGenerator
import io.github.stream29.langchain4kt2.core.Generator
import io.github.stream29.langchain4kt2.core.appendInputOn
import io.github.stream29.langchain4kt2.core.generateBy
import io.github.stream29.langchain4kt2.core.mapInput
import org.springframework.ai.chat.messages.*
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse

public fun <Response> ConfiguredGenerator<PromptBuilder, Response>.generateByMessages() =
    generateBy(PromptBuilder::messages)

public fun SpringAiMessage.textOrNull(): String? = when (val type = messageType) {
    MessageType.ASSISTANT -> (this as? AssistantMessage)?.text
    MessageType.USER -> (this as? UserMessage)?.text
    MessageType.SYSTEM -> (this as? SystemMessage)?.text
    MessageType.TOOL -> (this as? ToolResponseMessage)?.text
}

public fun SpringAiMessage.text() = textOrNull() ?: error("Getting text from $this")

public fun ChatResponse.singleText() = singleTextOrNull() ?: error("No text in $this")

public fun ChatResponse.singleTextOrNull() = result?.output?.textOrNull()

public fun <Output> Generator<List<SpringAiMessage>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(UserMessage(it)) }

public fun <Output> Generator<List<SpringAiMessage>, Output>.addSystemMessage(text: String) =
    appendInputOn(SystemMessage(text))