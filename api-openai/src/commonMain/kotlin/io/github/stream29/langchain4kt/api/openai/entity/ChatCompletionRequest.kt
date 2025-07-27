package io.github.stream29.langchain4kt.api.openai.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

@Serializable
public data class ChatCompletionRequest(
    public val model: String,
    public val messages: List<ChatMessage>,
    @SerialName("reasoning_effort")
    public val reasoningEffort: String? = null,
    public val temperature: Double? = null,
    @SerialName("top_p")
    public val topP: Double? = null,
    public val n: Int? = null,
    public val stop: List<String>? = null,
    public val store: Boolean? = null,
    @SerialName("max_tokens")
    public val maxTokens: Int? = null,
    @SerialName("max_completion_tokens")
    public val maxCompletionTokens: Int? = null,
    @SerialName("presence_penalty")
    public val presencePenalty: Double? = null,
    @SerialName("frequency_penalty")
    public val frequencyPenalty: Double? = null,
    @SerialName("logit_bias")
    public val logitBias: Map<String, Int>? = null,
    public val user: String? = null,
    @SerialName("response_format")
    public val responseFormat: ChatResponseFormat? = null,
    public val tools: List<Tool>? = null,
    @SerialName("tool_choice")
    public val toolChoice: ToolChoice? = null,
    public val seed: Int? = null,
    public val logprobs: Boolean? = null,
    @SerialName("top_logprobs")
    public val topLogprobs: Int? = null,
    @SerialName("instance_id")
    public val instanceId: String? = null,
    @SerialName("stream_options")
    public val streamOptions: StreamOptions? = null,
    public val stream: Boolean = true,
)

@Serializable
public data class ChatResponseFormat(
    @SerialName("type")
    public val type: String,
    @SerialName("json_schema")
    public val jsonSchema: JsonSchema? = null
)

@Serializable
public data class Tool(
    public val type: String,
    public val function: FunctionTool,
)

@Serializable
public data class FunctionTool(
    public val name: String,
    public val parameters: JsonObject? = null,
    public val description: String? = null
)

@Serializable
public data class JsonSchema(
    @SerialName("name")
    public val name: String? = null,
    @SerialName("schema")
    public val schema: JsonObject,
    @SerialName("strict")
    public val strict: Boolean? = null
)


@Serializable(with = ToolChoiceSerializer::class)
public sealed interface ToolChoice {
    @JvmInline
    @Serializable
    public value class Mode(public val value: String) : ToolChoice

    @Serializable
    public data class Named(
        @SerialName("type")
        public val type: String? = null,
        @SerialName("function")
        public val function: FunctionToolChoice? = null,
    ) : ToolChoice
}


@Serializable
public data class FunctionToolChoice(public val name: String)

@Serializable
public data class StreamOptions(
    @SerialName("include_usage")
    public val includeUsage: Boolean? = null,
)