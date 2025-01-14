package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.output.Response
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.measureTimedValue

public typealias ChatApiProviderPlugin<T, R> = suspend (Context, ChatApiProvider<T>) -> Response<R>

public class PluginChatApiProvider<T, R>(
    public val baseChatApiProvider: ChatApiProvider<T>,
    public val aroundGenerate: ChatApiProviderPlugin<T, R>,
) : ChatApiProvider<R> {
    override suspend fun generate(context: Context): Response<R> {
        return aroundGenerate(context, baseChatApiProvider)
    }
}

public object Plugins {
    public inline fun <T> logging(
        crossinline output: (String) -> Unit = ::println,
        crossinline successFormat: (Context, Response<T>, Duration) -> String = { context, response, duration ->
            "Generation completed in $duration with context \n$context\n and response \n$response"
        },
        crossinline failureFormat: (Context, Throwable, Duration) -> String = { context, throwable, duration ->
            "Generation failed in $duration with context \n$context\n and exception \n$throwable"
        },
    ): ChatApiProviderPlugin<T, T> = { context, chatApiProvider ->
        val timedValue = measureTimedValue {
            runCatching {
                chatApiProvider.generate(context)
            }
        }
        val duration = timedValue.duration
        timedValue.value.onSuccess { response ->
            output(successFormat(context, response, duration))
        }.onFailure { throwable ->
            output(failureFormat(context, throwable, duration))
        }.getOrThrow()
    }

    public fun <T> retryOnFail(
        times: Int = 5,
        delayMillis: Long = 0,
    ): ChatApiProviderPlugin<T, T> = plugin@{ context, chatApiProvider ->
        var count = 0
        while (true) {
            runCatching {
                return@plugin chatApiProvider.generate(context)
            }.onFailure {
                if (count >= times) throw GenerationException(
                    "Generation failed after $times retries with context: \n$context",
                    it
                )
                count++
                delay(delayMillis)
            }
        }
        TODO("Unreachable code")
    }

    public fun <T> retryOnResponse(
        times: Int = 5,
        delayMillis: Long = 0,
        retryOn: (Response<T>) -> Boolean,
    ): ChatApiProviderPlugin<T, T> = plugin@{ context, chatApiProvider ->
        var count = 0
        while (true) {
            runCatching {
                chatApiProvider.generate(context)
            }.onFailure {
                if (count >= times) throwRetryException(times, context, it)
                delay(delayMillis)
            }.onSuccess {
                if (!retryOn(it)) return@plugin it
                if (count >= times) throwRetryException(times, context, it.message)
            }
            count++
        }
        TODO("Unreachable code")
    }

    private fun throwRetryException(times: Int, context: Context, cause: Throwable): Nothing =
        throw GenerationException("Generation failed after $times retries with context: \n$context", cause)

    private fun throwRetryException(times: Int, context: Context, responseText: String): Nothing =
        throwRetryException(times, context, IllegalArgumentException("Response requires retry, content: \n$responseText"))
}