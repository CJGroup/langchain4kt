package io.github.stream29.langchain4kt.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentRequest
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.common.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.common.shared.Content
import dev.shreyaspatil.ai.client.generativeai.common.shared.SafetySetting
import dev.shreyaspatil.ai.client.generativeai.common.shared.TextPart
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.serialization.ExperimentalSerializationApi

internal fun MessageSender.toGeminiSender() = when (this) {
    MessageSender.Model -> "model"
    MessageSender.User -> "user"
}

@OptIn(ExperimentalSerializationApi::class)
internal fun constructRequest(
    modelName: String,
    context: Context,
    safetySettings: List<SafetySetting>?,
    generationConfig: GenerationConfig?
) =
    GenerateContentRequest(
        modelName,
        context.history.map { Content(it.sender.toGeminiSender(), listOf(TextPart(it.content))) },
        safetySettings,
        generationConfig,
        null,
        null,
        context.systemInstruction?.let { Content("system", listOf(TextPart(it))) },
    )

@OptIn(ExperimentalSerializationApi::class)
internal val GenerateContentResponse.text: String?
    get() =
        candidates?.firstOrNull()
            ?.content
            ?.parts
            ?.asSequence()
            ?.map { it as? TextPart }
            ?.filterNotNull()
            ?.joinToString("") { it.text }