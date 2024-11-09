package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.message.MessageSender

/**
 * A respondent that generate a single response for a single message.
 *
 * It should be stateless and concurrency-safe.
 */
public interface Respondent {
    /**
     * Generate a response for the given message.
     */
    public suspend fun chat(message: String): String
}

/**
 * Build a [SimpleRespondent] with a [ChatApiProvider] and [systemInstruction].
 *
 * @param systemInstruction The system instruction that will be used when generating responses.
 */
public fun ChatApiProvider<*>.asRespondent(systemInstruction: String? = null): SimpleRespondent =
    SimpleRespondent(this, systemInstruction)

/**
 * Build a [WrappedRespondent] with a [Respondent] and [wrapper].
 *
 * @param wrapper The function that processes the message before sending it to the base [Respondent].
 */
public fun Respondent.wrap(wrapper: (String) -> String): Respondent =
    WrappedRespondent(this, wrapper)

/**
 * A respondent that simply generates a response with [apiProvider] and [systemInstruction].
 */
public data class SimpleRespondent(
    val apiProvider: ChatApiProvider<*>,
    val systemInstruction: String? = null
) : Respondent {
    override suspend fun chat(message: String): String {
        return apiProvider.generateFrom {
            systemInstruction(systemInstruction)
            MessageSender.User.chat(message)
        }.message
    }
}

/**
 * A respondent that wraps the message with [wrapper] before sending it to [baseRespondent].
 */
public data class WrappedRespondent(
    val baseRespondent: Respondent,
    val wrapper: (String) -> String
) : Respondent {
    override suspend fun chat(message: String): String {
        return baseRespondent.chat(wrapper(message))
    }
}
