package io.github.stream29.langchain4kt.api.openai.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
public data class ChatCompletionFunction(
    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum
     * length of 64.
     */
    @SerialName("name")public  val name: String,
    /**
     * The description of what the function does.
     */
    @SerialName("description")public  val description: String? = null,
    /**
     * The parameters the functions accept, described as a JSON Schema object.
     * See the [guide](https://github.com/aallam/openai-kotlin/blob/main/guides/ChatFunctionCall.md) for examples, and the [JSON Schema reference](https://json-schema.org/understanding-json-schema/) for documentation about the format.
     *
     * To describe a function that accepts no parameters, provide [Parameters.Empty]`.
     */
    @SerialName("parameters")public  val parameters: JsonObject,
)

