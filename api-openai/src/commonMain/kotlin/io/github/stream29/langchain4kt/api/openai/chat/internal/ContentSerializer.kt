package io.github.stream29.langchain4kt.api.openai.chat.internal

import io.github.stream29.langchain4kt.api.openai.chat.Content
import io.github.stream29.langchain4kt.api.openai.chat.ListContent
import io.github.stream29.langchain4kt.api.openai.chat.TextContent
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [Content].
 */
internal class ContentSerializer : JsonContentPolymorphicSerializer<Content>(Content::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Content> {
        return when (element) {
            is JsonPrimitive -> TextContent.serializer()
            is JsonArray -> ListContent.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}
