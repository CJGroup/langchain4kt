package io.github.stream29.langchain4kt.api.openai.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * An object specifying the format that the model must output.
 */
@Serializable
public data class ChatResponseFormat(
    /**
     * Response format type.
     */
    @SerialName("type")public  val type: String,

    /**
     * Optional JSON schema specification when type is "json_schema"
     */
    @SerialName("json_schema")public  val jsonSchema: JsonSchema? = null
)

/**
 * Specification for JSON schema response format
 */
@Serializable
public data class JsonSchema(
    /**
     * Optional name for the schema
     */
    @SerialName("name")public  val name: String? = null,

    /**
     * The JSON schema specification
     */
    @SerialName("schema")public  val schema: JsonObject,

    /**
     * Whether to enforce strict schema validation
     */
    @SerialName("strict")public  val strict: Boolean? = null
)
