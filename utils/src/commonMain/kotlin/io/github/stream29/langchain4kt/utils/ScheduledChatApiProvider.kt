package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

/**
 * A ChatApiProvider that uses a ChatApiProviderScheduler to generate response.
 * @property scheduler [ChatApiProviderScheduler] to use
 */
public class ScheduledChatApiProvider<T>(
    public val scheduler: ChatApiProviderScheduler<T>,
) : ChatApiProvider<T> {
    override suspend fun generate(context: Context): Response<T> {
        return scheduler.provide { it.generate(context) }
    }
}