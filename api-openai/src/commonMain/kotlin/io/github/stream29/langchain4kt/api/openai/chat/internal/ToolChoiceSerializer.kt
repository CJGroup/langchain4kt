package io.github.stream29.langchain4kt.api.openai.chat.internal

import io.github.stream29.langchain4kt.api.openai.chat.ToolChoice
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [ToolChoice].
 */
internal class ToolChoiceSerializer : JsonContentPolymorphicSerializer<ToolChoice>(ToolChoice::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ToolChoice> {
        return when (element) {
            is JsonPrimitive -> ToolChoice.Mode.serializer()
            is JsonObject -> ToolChoice.Named.serializer()
            else -> throw UnsupportedOperationException("Unsupported JSON element: $element")
        }
    }
}
