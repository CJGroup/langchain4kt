package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public data class BalancingApiProvider(
    val apiProviders: List<ChatApiProvider<*>>,
    var currentApiProviderIndex: Int = apiProviders.size
) : ChatApiProvider<Any?> {
    private val mutex = Mutex()

    @Suppress("unchecked_cast")
    override suspend fun generate(context: Context): Response<Any?> {
        val apiProvider = mutex.withLock {
            currentApiProviderIndex++
            currentApiProviderIndex %= apiProviders.size
            apiProviders[currentApiProviderIndex]
        }
        val response = apiProvider.generate(context)
        return response as Response<Any?>
    }
}