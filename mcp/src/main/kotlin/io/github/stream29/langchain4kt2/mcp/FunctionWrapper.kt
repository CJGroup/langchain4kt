package io.github.stream29.langchain4kt2.mcp

import kotlinx.serialization.Serializable

@PublishedApi
@Suppress("UnusedReceiverParameter")
internal inline fun <T, R> ServerAdapter.boxedParam(crossinline f: suspend (T) -> R): suspend (Box<T>) -> R =
    { f(it.value) }

@PublishedApi
internal inline fun <T, reified R> ServerAdapter.safeReturn(crossinline f: suspend (T) -> R): suspend (T) -> String =
    { param ->
        val returnValue = f(param)
        when {
            returnValue is String -> returnValue
            else -> encodeToString(returnValue)
        }
    }

@Serializable
@PublishedApi
internal data class Box<T>(
    val value: T,
)