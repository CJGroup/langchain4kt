package io.github.stream29.langchain4kt.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

/**
 * Custom strategy for scheduling [ApiProvider] usage.
 */
public interface DispatchStrategy<ApiProvider> {
    /**
     * Provides a [ApiProvider] to the consumer.
     */
    public suspend fun <R> dispatch(block: suspend (ApiProvider) -> R): R


    /**
     * Simple polling strategy for scheduling [ApiProvider].
     * This is useful when you have multiple [ApiProvider]s and you want to balance the load between them evenly.
     * @property apiProviders List of [ApiProvider]s to poll
     */
    public class Polling<ApiProvider>(
        public val apiProviders: List<ApiProvider>,
    ) : DispatchStrategy<ApiProvider> {
        init {
            require(apiProviders.isNotEmpty()) { "apiProviders must not be empty" }
        }

        private val mutex = Mutex()

        @Volatile
        private var index = 0

        override suspend fun <R> dispatch(block: suspend (ApiProvider) -> R): R {
            val provider = mutex.withLock {
                apiProviders[index].also {
                    index = (index + 1) % apiProviders.size
                }
            }
            return block(provider)
        }
    }

    /**
     * Weighted polling strategy for scheduling [ApiProvider].
     * Every [ApiProvider] will be used n times in an iteration where n is the weight of the [ApiProvider].
     * @property apiProviders Map of [ApiProvider] to poll with their respective weights
     */
    public class WeightedPolling<ApiProvider>(
        public val apiProviders: Map<ApiProvider, Int>
    ) : DispatchStrategy<ApiProvider> {
        init {
            require(apiProviders.isNotEmpty()) { "chatApiProviders must not be empty" }
        }

        private val mutex = Mutex()
        private val iterator = sequence {
            while (true)
                for ((provider, weight) in apiProviders)
                    repeat(weight) { yield(provider) }
        }.iterator()

        override suspend fun <R> dispatch(block: suspend (ApiProvider) -> R): R {
            val provider = mutex.withLock {
                iterator.next()
            }
            return block(provider)
        }
    }

    /**
     * Random strategy for scheduling [ApiProvider].
     * @property apiProviders List of [ApiProvider]s to choose from
     */
    public class Random<ApiProvider>(
        public val apiProviders: List<ApiProvider>,
    ) : DispatchStrategy<ApiProvider> {
        init {
            require(apiProviders.isNotEmpty()) { "apiProviders must not be empty" }
        }

        private val random = kotlin.random.Random

        override suspend fun <R> dispatch(block: suspend (ApiProvider) -> R): R {
            val provider = apiProviders[random.nextInt(apiProviders.size)]
            return block(provider)
        }
    }

    /**
     * Weighted random strategy for scheduling [ApiProvider].
     * @property apiProviders Map of [ApiProvider] to choose from with their respective weights
     */
    public class WeightedRandom<ApiProvider>(
        apiProviders: Map<ApiProvider, Int>
    ) : DispatchStrategy<ApiProvider> {
        init {
            require(apiProviders.isNotEmpty()) { "chatApiProviders must not be empty" }
        }

        private val random = kotlin.random.Random
        private val apiProviderList = apiProviders.keys.toList()
        private val acc = apiProviders.values
            .asSequence()
            .runningFold(0) { acc, weight -> acc + weight }
            .drop(1)
            .distinct()
            .toList()
        private val max = acc.last()

        override suspend fun <R> dispatch(block: suspend (ApiProvider) -> R): R {
            val index = acc.binarySearch(random.nextInt(max)).let { if (it >= 0) it + 1 else -it - 1 }
            val provider = apiProviderList[index]
            return block(provider)
        }
    }
}