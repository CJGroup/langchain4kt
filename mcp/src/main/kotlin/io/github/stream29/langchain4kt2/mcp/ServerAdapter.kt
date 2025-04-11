package io.github.stream29.langchain4kt2.mcp

import io.github.stream29.jsonschemagenerator.SchemaGenerator
import io.github.stream29.jsonschemagenerator.schemaOf
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.*

public class ServerAdapter(
    public val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    },
    public val schemaGenerator: SchemaGenerator = SchemaGenerator.default
) {
    public companion object {
        public val default: ServerAdapter = ServerAdapter()
    }
}

public inline fun <reified T : Any, reified R> Server.addTool(
    name: String,
    description: String?,
    crossinline from: suspend (T) -> R
) {
    val tool = ServerAdapter.default.makeTool(name, description, from)
    addTools(listOf(tool))
}

public inline fun <reified T : Any, reified R> ServerAdapter.makeTool(
    name: String,
    description: String?,
    crossinline from: suspend (T) -> R
): RegisteredTool =
    if (schemaOf<T>().contains("properties")) makeToolUnsafe(name, description, safeReturn(from))
    else makeToolUnsafe(name, description, boxedParam(safeReturn(from)))

@PublishedApi
internal inline fun <reified T : Any> ServerAdapter.makeToolUnsafe(
    name: String,
    description: String?,
    noinline handler: suspend (T) -> String
): RegisteredTool {
    val inputSchema = schemaOf<T>()
    return RegisteredTool(
        Tool(
            name = name,
            description = description,
            inputSchema = Tool.Input(
                properties = inputSchema["properties"]!!.jsonObject,
                required = inputSchema["required"]?.jsonArray?.map { it.jsonPrimitive.content },
            )
        )
    ) handler@{ (_, arguments, _) ->
        try {
            val param = decodeFromJsonElement<T>(arguments)
            val returnValue = handler(param)
            CallToolResult.ok(returnValue)
        } catch (e: Throwable) {
            CallToolResult.error(e.toString())
        }
    }
}

@PublishedApi
internal inline fun <reified T> ServerAdapter.schemaOf(): JsonObject =
    schemaGenerator.schemaOf<T>().jsonObject

@PublishedApi
internal inline fun <reified T> ServerAdapter.decodeFromJsonElement(element: JsonElement): T =
    json.decodeFromJsonElement<T>(element)

@PublishedApi
internal inline fun <reified T> ServerAdapter.encodeToString(value: T): String =
    json.encodeToString(value)
