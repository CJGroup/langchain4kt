package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

/**
 * A respondent that generate a single streaming response for a single message.
 *
 * It should be stateless and concurrency-safe.
 */
public interface StreamRespondent {
    /**
     * Generate a streaming response for the given message.
     */
    public suspend fun chat(message: String): Flow<String>
}

/**
 * Build a [SimpleRespondent] with a [ChatApiProvider] and [systemInstruction].
 *
 * @param systemInstruction The system instruction that will be used when generating responses.
 */
public fun StreamChatApiProvider<*>.asStreamRespondent(systemInstruction: String? = null): SimpleStreamRespondent =
    SimpleStreamRespondent(this, systemInstruction)

/**
 * Build a [Respondent] with a [StreamRespondent].
 */
public fun StreamRespondent.collecting(): CollectingRespondent =
    CollectingRespondent(this)

/**
 * A respondent that simply generates a response with [apiProvider] and [systemInstruction].
 */
public data class SimpleStreamRespondent(
    val apiProvider: StreamChatApiProvider<*>,
    val systemInstruction: String? = null
) : StreamRespondent {
    override suspend fun chat(message: String): Flow<String> {
        return apiProvider.generateFrom {
            systemInstruction(systemInstruction)
            MessageSender.User.chat(message)
        }.message
    }
}

/**
 * A respondent that collects the message with [StringBuilder] and return it.
 */
public data class CollectingRespondent(
    val baseRespondent: StreamRespondent
) : Respondent {
    override suspend fun chat(message: String): String {
        return baseRespondent.chat(message).fold(StringBuilder()) { acc, value -> acc.append(value) }.toString()
    }
}