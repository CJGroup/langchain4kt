package io.github.stream29.langchain4kt.streaming

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking

/**
 * Blocking version of [StreamChatModel.chat]
 *
 * Can be used in Kotlin notebook.
 */
public fun StreamChatModel.chatBlocking(message: String): Flow<String> = runBlocking { chat(message) }

/**
 * Blocking version of [StreamRespondent.chat]
 *
 * Can be used in Kotlin notebook.
 */
public fun StreamRespondent.chatBlocking(message: String): Flow<String> = runBlocking { chat(message) }

/**
 * Blocking version of [Flow.collect]
 *
 * Can be used in Kotlin notebook.
 */
public fun <T> Flow<T>.collectBlocking(collector: FlowCollector<T>): Unit = runBlocking { collect(collector) }