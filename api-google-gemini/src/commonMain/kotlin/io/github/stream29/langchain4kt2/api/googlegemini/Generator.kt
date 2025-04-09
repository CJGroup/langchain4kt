package io.github.stream29.langchain4kt2.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentRequest
import io.github.stream29.langchain4kt2.core.ConfigurableGenerator
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
public fun APIController.asGenerator() = ConfigurableGenerator(::GenerateContentResponseBuilder) {
    generateContent(
        GenerateContentRequest(
            it.model,
            it.contents ?: error("empty content"),
            it.safetySettings,
            it.generationConfig,
            it.tools,
            it.toolConfig,
            it.systemInstruction
        )
    )
}

@OptIn(ExperimentalSerializationApi::class)
public fun APIController.asStreamingGenerator() = ConfigurableGenerator(::GenerateContentResponseBuilder) {
    generateContentStream(
        GenerateContentRequest(
            it.model,
            it.contents ?: error("empty content"),
            it.safetySettings,
            it.generationConfig,
            it.tools,
            it.toolConfig,
            it.systemInstruction
        )
    )
}