package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.output.Response

public data class SwitchOnFailApiProvider(
    val apiProviders: List<ChatApiProvider<*>>,
    var currentApiProviderIndex: Int = 0,
    var currentFailSum: Int = 0
) : ChatApiProvider<Any?> {
    @Suppress("unchecked_cast")
    override suspend fun generate(context: Context): Response<Any?> {
        try {
            val apiProvider = apiProviders[currentApiProviderIndex]
            val response = apiProvider.generate(context)
            currentFailSum = 0
            return response as Response<Any?>
        } catch (e: Exception) {
            currentApiProviderIndex++
            currentApiProviderIndex %= apiProviders.size
            currentFailSum++
            if (currentFailSum == apiProviders.size) {
                throw GenerationException("All API providers failed", e)
            }
            return generate(context)
        }
    }
}