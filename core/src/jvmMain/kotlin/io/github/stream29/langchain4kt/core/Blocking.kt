package io.github.stream29.langchain4kt.core

import kotlinx.coroutines.runBlocking

/**
 * Blocking version of [ChatModel.chat]
 *
 * Can be used in Kotlin notebook.
 */
public fun ChatModel.chatBlocking(message: String): String = runBlocking { chat(message) }

/**
 * Blocking version of [Respondent.chat]
 *
 * Can be used in Kotlin notebook.
 */
public fun Respondent.chatBlocking(message: String): String = runBlocking { chat(message) }