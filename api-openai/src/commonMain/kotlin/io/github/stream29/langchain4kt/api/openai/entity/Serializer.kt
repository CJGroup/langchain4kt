package io.github.stream29.langchain4kt.api.openai.entity

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class ContentSerializer : JsonContentPolymorphicSerializer<Content>(Content::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Content> {
        return when (element) {
            is JsonPrimitive -> TextContent.serializer()
            is JsonArray -> ListContent.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}

internal class ToolChoiceSerializer : JsonContentPolymorphicSerializer<ToolChoice>(ToolChoice::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ToolChoice> {
        return when (element) {
            is JsonPrimitive -> ToolChoice.Mode.serializer()
            is JsonObject -> ToolChoice.Named.serializer()
            else -> throw UnsupportedOperationException("Unsupported JSON element: $element")
        }
    }
}