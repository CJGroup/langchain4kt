@file:OptIn(ExperimentalSerializationApi::class)

package io.github.stream29.langchain4kt.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.common.shared.Content
import dev.shreyaspatil.ai.client.generativeai.common.shared.TextPart
import io.github.stream29.langchain4kt.core.*
import kotlinx.serialization.ExperimentalSerializationApi

public fun <Response> ConfiguredGenerator<GenerateContentResponseBuilder, Response>.generateByMessages() =
    generateByNotNullable(GenerateContentResponseBuilder::contents)

public fun Content.singleTextOrNull() = this.parts.firstOrNull()?.let { (it as? TextPart)?.text }

public fun Content.singleText() = singleTextOrNull() ?: error("Getting text from $this")

public fun GenerateContentResponse.singleText() = singleTextOrNull() ?: error("No text in $this")

public fun GenerateContentResponse.singleTextOrNull() = candidates?.firstOrNull()?.content?.singleTextOrNull()

public fun <Output> Generator<List<Content>, Output>.mapInputFromText() =
    mapInput { it: String -> listOf(Content("user", listOf(TextPart(it)))) }

public fun <Output> ConfiguredGenerator<GenerateContentResponseBuilder, Output>.setSystemInstruction(text: String) =
    configure { systemInstruction = Content("system", listOf(TextPart(text))) }