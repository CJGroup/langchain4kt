package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.message.*
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.response.ChatResponseMetadata
import io.github.stream29.langchain4kt.core.ApiProvider
import io.github.stream29.langchain4kt.core.Response
import io.github.stream29.union.Union5
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public typealias Langchain4jHistoryType = Union5<
        SystemMessage,
        UserMessage,
        AiMessage,
        ToolExecutionResultMessage,
        CustomMessage
        >

public fun ChatLanguageModel.toApiProvider(): ApiProvider<Langchain4jHistoryType, Response<AiMessage, ChatResponseMetadata>> =
    { history ->
        suspendCoroutine { continuation ->
            val rawResponse = chat(history.map { it.value as ChatMessage })
            continuation.resume(
                Response(
                    rawResponse.aiMessage()!!,
                    rawResponse.metadata()!!
                )
            )
        }
    }