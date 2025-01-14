package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

/**
 * Custom strategy for scheduling ChatApiProvider.
 */
public interface ChatApiProviderScheduler<T> {
    /**
     * Provides a ChatApiProvider to the consumer.
     */
    public suspend fun <R> provide(block: suspend (ChatApiProvider<T>) -> R): R
}

/**
 * Simple polling strategy for scheduling ChatApiProvider.
 * This is useful when you have multiple ChatApiProviders and you want to balance the load between them evenly.
 * @property chatApiProviders List of ChatApiProviders to poll
 */
public class Polling<T>(
    public val chatApiProviders: List<ChatApiProvider<T>>,
) : ChatApiProviderScheduler<T> {
    init {
        require(chatApiProviders.isNotEmpty()) { "chatApiProviders must not be empty" }
    }

    private val mutex = Mutex()

    @Volatile
    private var index = 0

    override suspend fun <R> provide(block: suspend (ChatApiProvider<T>) -> R): R {
        val provider = mutex.withLock {
            chatApiProviders[index].also {
                index = (index + 1) % chatApiProviders.size
            }
        }
        return block(provider)
    }
}

/**
 * Weighted polling strategy for scheduling ChatApiProvider.
 * Every ChatApiProvider will be used n times in a iteration where n is the weight of the ChatApiProvider.
 * @property chatApiProviders Map of ChatApiProviders to poll with their respective weights
 */
public class WeightedPolling<T>(
    public val chatApiProviders: Map<ChatApiProvider<T>, Int>
) : ChatApiProviderScheduler<T> {
    init {
        require(chatApiProviders.isNotEmpty()) { "chatApiProviders must not be empty" }
    }

    private val mutex = Mutex()
    private val iterator = sequence {
        while (true)
            for ((provider, weight) in chatApiProviders)
                repeat(weight) { yield(provider) }
    }.iterator()

    override suspend fun <R> provide(block: suspend (ChatApiProvider<T>) -> R): R {
        val provider = mutex.withLock {
            iterator.next()
        }
        return block(provider)
    }
}