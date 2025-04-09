package io.github.stream29.langchain4kt2.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.common.client.Tool
import dev.shreyaspatil.ai.client.generativeai.common.client.ToolConfig
import dev.shreyaspatil.ai.client.generativeai.common.shared.Content
import dev.shreyaspatil.ai.client.generativeai.common.shared.SafetySetting
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
public class GenerateContentResponseBuilder {
    public var model: String? = null
    public var contents: List<Content>? = null
    public var safetySettings: List<SafetySetting>? = null
    public var generationConfig: GenerationConfig? = null
    public var tools: List<Tool>? = null
    public var toolConfig: ToolConfig? = null
    public var systemInstruction: Content? = null
}