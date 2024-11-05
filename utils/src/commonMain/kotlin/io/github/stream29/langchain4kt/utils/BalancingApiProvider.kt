package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

data class BalancingApiProvider(
    val apiProviders: List<ChatApiProvider<*>>,
    var currentApiProviderIndex: Int = 0
): ChatApiProvider<Any?> {
    @Suppress("unchecked_cast")
    override suspend fun generate(context: Context): Response<Any?> {
        val apiProvider = apiProviders[currentApiProviderIndex]
        val response = apiProvider.generate(context)
        currentApiProviderIndex++
        currentApiProviderIndex %= apiProviders.size
        return response as Response<Any?>
    }
}