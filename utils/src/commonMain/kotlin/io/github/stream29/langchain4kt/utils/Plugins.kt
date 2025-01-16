package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.Respondent
import kotlinx.coroutines.delay
import kotlin.time.Duration
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