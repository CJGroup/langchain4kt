package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.output.GenerationException
import kotlin.time.Duration
import kotlin.time.measureTimedValue

public typealias Plugin<C> = (C) -> C
public typealias Generator<T, R> = suspend (T) -> R
public typealias PluginImplementation<C, T, R> = suspend (C, T) -> R
public typealias PluginReceiver<C, T, R> = (C, Plugin<Generator<T, R>>) -> C

public inline fun <T, R> curry(crossinline block: suspend (Generator<T, R>, T) -> R): Plugin<Generator<T, R>> =
    { p0 -> { p1 -> block(p0, p1) } }

/**
 * Collection of basic plugins.
 */
public object Plugins {
    public inline fun <T, R> logging(
        crossinline output: (String) -> Unit = ::println,
        crossinline successFormat: (T, R, Duration) -> String = { param, result, duration ->
            "Generation completed in $duration with param \n$param\n and result \n$result"
        },
        crossinline failureFormat: (T, Throwable, Duration) -> String = { param, throwable, duration ->
            "Generation failed in $duration with param \n$param\n and exception \n$throwable"
        },
    ): Plugin<Generator<T, R>> = curry { respondent, param ->
        val (result, duration) = measureTimedValue {
            runCatching {
                respondent(param)
            }
        }
        result.onSuccess { response ->
            output(successFormat(param, response, duration))
        }.onFailure { throwable ->
            output(failureFormat(param, throwable, duration))
        }.getOrThrow()
    }


//    /**
//     * Plugin that logs the generation process.
//     * @param output Destination to output the log
//     * @param successFormat Format for successful generation
//     * @param failureFormat Format for failed generation
//     */
//    public inline fun <T, R> logging(
//        crossinline output: (String) -> Unit = ::println,
//        crossinline successFormat: (T, R, Duration) -> String = { param, result, duration ->
//            "Generation completed in $duration with param \n$param\n and result \n$result"
//        },
//        crossinline failureFormat: (T, Throwable, Duration) -> String = { param, throwable, duration ->
//            "Generation failed in $duration with param \n$param\n and exception \n$throwable"
//        },
//    ): GeneratorPlugin<T, R> = { param, generate ->
//        val timedValue = measureTimedValue {
//            runCatching {
//                generate(param)
//            }
//        }
//        val duration = timedValue.duration
//        timedValue.value.onSuccess { response ->
//            output(successFormat(param, response, duration))
//        }.onFailure { throwable ->
//            output(failureFormat(param, throwable, duration))
//        }.getOrThrow()
//    }
//
//    /**
//     * Plugin that retries the generation process on failure.
//     * @param times Number of times to retry
//     * @param delayMillis Delay between retries
//     */
//    public fun <T, R> retryOnFail(
//        times: Int = 5,
//        delayMillis: Long = 0,
//        retryExceeded: (param: T, times: Int, throwable: Throwable) -> Nothing = ::throwRetryException
//    ): GeneratorPlugin<T, R> = plugin@{ param, generate ->
//        var count = 0
//        while (true) {
//            runCatching {
//                return@plugin generate(param)
//            }.onFailure {
//                if (count >= times) retryExceeded(param, count, it)
//                count++
//                delay(delayMillis)
//            }
//        }
//        TODO("Unreachable code")
//    }

//    public inline fun metaprompt(
//        crossinline metaprompt: suspend (String) -> String
//    ): RespondentPlugin = { message, generate ->
//        generate(generate(metaprompt(message)))
//    }

    public fun <T> throwRetryException(param: T, times: Int, cause: Throwable): Nothing =
        throw GenerationException("Generation failed after $times retries with context: \n$param", cause)
}