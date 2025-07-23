package io.github.stream29.langchain4kt.api.openai.chat

import io.github.stream29.langchain4kt.api.openai.chat.internal.ToolChoiceSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Controls which (if any) function is called by the model.
 */
@Serializable(with = ToolChoiceSerializer::class)
public sealed interface ToolChoice {

    /**
     * Represents a function call mode.
     * - `"none"` means the model will not call a function and instead generates a message.
     * - `"auto"` means the model can pick between generating a message or calling a function.
     */
    @JvmInline
    @Serializable
    public value class Mode(public val value: String) : ToolChoice

    /**
     * Specifies a tool the model should use.
     */
    @Serializable
    public data class Named(
        @SerialName("type")
        public val type: String? = null,
        @SerialName("function")
        public val function: FunctionToolChoice? = null,
    ) : ToolChoice
}

/**
 * Represents the function to call by the model.
 */
@Serializable
public data class FunctionToolChoice(public val name: String)
