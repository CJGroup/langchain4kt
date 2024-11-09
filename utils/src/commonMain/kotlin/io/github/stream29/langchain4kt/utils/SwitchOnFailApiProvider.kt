package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.output.Response
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public data class SwitchOnFailApiProvider<MetaInfo>(
    val apiProviders: List<ChatApiProvider<MetaInfo>>,
    var currentApiProviderIndex: Int = 0,
    var currentFailSum: Int = 0
) : ChatApiProvider<MetaInfo> {
    private val mutex = Mutex()
    override suspend fun generate(context: Context): Response<MetaInfo> {
        suspend fun generateRecursively(context: Context): Response<MetaInfo>{
            try {
                val apiProvider = apiProviders[currentApiProviderIndex]
                val response = apiProvider.generate(context)
                currentFailSum = 0
                return response
            } catch (e: Exception) {
                currentApiProviderIndex++
                currentApiProviderIndex %= apiProviders.size
                currentFailSum++
                if (currentFailSum == apiProviders.size) {
                    throw GenerationException("All API providers failed", e)
                }
                return generateRecursively(context)
            }
        }
        return mutex.withLock {
            generateRecursively(context)
        }
    }
}