package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.Respondent
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.streaming.StreamResponse
import io.github.stream29.langchain4kt.utils.Plugins.metaprompt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

/**
 * Type alias for a plugin.
 * This does not change the type of the original instance, only enhances it.
 */
public typealias Plugin<C> = (C) -> C
/**
 * Type alias for a generator.
 * This represents a suspend function that generates a result from a given input.
 */
public typealias Generator<T, R> = suspend (T) -> R

/**
 * Collection of basic plugins.
 */
public object Plugins {
    /**
     * Convention to build a plugin logic by currying.
     */
    public inline fun <T, R> curry(crossinline block: suspend (Generator<T, R>, T) -> R): Plugin<Generator<T, R>> =
        { p0 -> { p1 -> block(p0, p1) } }

    /**
     * Plugin that logs the generation process.
     * @param output Destination to output the log
     * @param successFormat Format for successful generation
     * @param failureFormat Format for failed generation
     */
    public inline fun <T, R> logging(
        crossinline output: (String) -> Unit = ::println,
        crossinline successFormat: (T, R, Duration) -> String = { param, result, duration ->
            "Generation completed in $duration with param \n$param\n and result \n$result"
        },
        crossinline failureFormat: (T, Throwable, Duration) -> String = { param, throwable, duration ->
            "Generation failed in $duration with param \n$param\n and exception \n$throwable"
        },
    ): Plugin<Generator<T, R>> = curry { generate, param ->
        val (result, duration) = measureTimedValue {
            runCatching {
                generate(param)
            }
        }
        result.onSuccess { response ->
            output(successFormat(param, response, duration))
        }.onFailure { throwable ->
            output(failureFormat(param, throwable, duration))
        }.getOrThrow()
    }

    @JvmName("loggingOnComplete4Response")
    public inline fun <MetaInfo> loggingOnComplete(
        crossinline output: (String) -> Unit = ::println,
        crossinline successFormat: (Context, Response<MetaInfo>, Duration) -> String = { context, response, duration ->
            "Generation completed in $duration with context \n$context\n and response \n$response"
        },
        crossinline failureFormat: (Context, Throwable, Duration) -> String = { message, throwable, duration ->
            "Generation failed in $duration with message \n$message\n and exception \n$throwable"
        }
    ): Plugin<Generator<Context, StreamResponse<MetaInfo>>> = curry { generate, param ->
        val timestamp = TimeSource.Monotonic.markNow()
        val response = runCatching { generate(param) }.onFailure { e ->
            output(failureFormat(param, e, timestamp.elapsedNow()))
        }.getOrThrow()

        val mutex = Mutex()
        val buffer = StringBuilder()
        val deferredMetaInfo = response.metaInfo
        val wrappedFlow = response.message
            .onEach {
                mutex.withLock { buffer.append(it) }
            }.onCompletion { e ->
                if (e == null) {
                    CoroutineScope(currentCoroutineContext()).launch {
                        runCatching {
                            val message = buffer.toString()
                            val metaInfo = deferredMetaInfo.await()
                            Response(message, metaInfo)
                        }.onSuccess {
                            output(successFormat(param, it, timestamp.elapsedNow()))
                        }.onFailure { throwable ->
                            output(failureFormat(param, throwable, timestamp.elapsedNow()))
                        }
                    }
                } else {
                    output(failureFormat(param, e, timestamp.elapsedNow()))
                }
            }
        StreamResponse(wrappedFlow, deferredMetaInfo)
    }

    @JvmName("loggingOnComplete4String")
    public inline fun loggingOnComplete(
        crossinline output: (String) -> Unit = ::println,
        crossinline successFormat: (String, String, Duration) -> String = { message, response, duration ->
            "Generation completed in $duration with message \n$message\n and response \n$response"
        },
        crossinline failureFormat: (String, Throwable, Duration) -> String = { message, throwable, duration ->
            "Generation failed in $duration with message \n$message\n and exception \n$throwable"
        }
    ): Plugin<Generator<String, Flow<String>>> = curry<String, Flow<String>> { generate, param ->
        val timestamp = TimeSource.Monotonic.markNow()
        val response = runCatching { generate(param) }.onFailure {
            output(failureFormat(param, it, timestamp.elapsedNow()))
        }.getOrThrow()
        val mutex = Mutex()
        val buffer = StringBuilder()
        response.onEach { mutex.withLock { buffer.append(it) } }
            .onCompletion { e ->
                if (e == null) output(successFormat(param, buffer.toString(), timestamp.elapsedNow()))
                else output(failureFormat(param, e, timestamp.elapsedNow()))
            }
    }


    /**
     * Plugin that retries the generation process on failure.
     * @param times Number of times to retry
     * @param beforeRetry You can log or delay before retrying
     * @param retryExceeded Action to take when retry limit is exceeded
     */
    public inline fun <T, R> retryOnFail(
        times: Int = 5,
        crossinline beforeRetry: suspend (Throwable) -> Unit = { delay(0) },
        crossinline retryExceeded: (param: T, times: Int, throwable: Throwable) -> Nothing = ::throwRetryException
    ): Plugin<Generator<T, R>> = curry plugin@{ generate, param ->
        var count = 0
        while (true) {
            runCatching {
                return@plugin generate(param)
            }.onFailure {
                if (count >= times) retryExceeded(param, count, it)
                count++
                beforeRetry(it)
            }
        }
        TODO("Unreachable code")
    }

    /**
     * Plugin that uses [metaprompt] to generate a prompt when responding.
     * Only for [Respondent].
     */
    public inline fun metaprompt(
        crossinline metaprompt: suspend (String) -> String
    ): Plugin<RespondentFunc> = curry { generate, message ->
        generate(generate(metaprompt(message)))
    }

    public fun <T> throwRetryException(param: T, times: Int, cause: Throwable): Nothing =
        throw GenerationException("Generation failed after $times retries with param: \n$param", cause)
}