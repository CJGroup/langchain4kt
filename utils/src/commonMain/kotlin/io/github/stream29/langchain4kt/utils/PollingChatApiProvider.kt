package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

/**
 * PollingChatApiProvider is a ChatApiProvider that polls multiple ChatApiProviders in a round-robin fashion.
 * This is useful when you have multiple ChatApiProviders and you want to balance the load between them.
 *
 * @property chatApiProviders List of ChatApiProviders to poll
 */
public class PollingChatApiProvider<T>(
    public val chatApiProviders: List<ChatApiProvider<T>>,
) : ChatApiProvider<T> {
    init {
        require(chatApiProviders.isNotEmpty()) { "Empty API list" }
    }

    @Volatile
    private var currentProviderIndex = 0
    private val mutex = Mutex()
    override suspend fun generate(context: Context): Response<T> {
        mutex.withLock {
            currentProviderIndex = (currentProviderIndex + 1) % chatApiProviders.size
        }
        return chatApiProviders[currentProviderIndex].generate(context)
    }
}