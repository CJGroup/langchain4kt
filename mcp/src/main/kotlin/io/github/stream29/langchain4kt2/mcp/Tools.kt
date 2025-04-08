package io.github.stream29.langchain4kt2.mcp

import io.github.stream29.jsonschemagenerator.SchemaGenerator
import io.github.stream29.jsonschemagenerator.schemaOf
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.*

public inline fun <reified ParamType : Any, reified ReturnType> Server.addTool(
    name: String,
    description: String,
    noinline from: (ParamType) -> ReturnType
) {
    val inputSchema = SchemaGenerator.default.schemaOf<ParamType>().jsonObject
    this.addTool(
        name = name,
        description = description,
        inputSchema = Tool.Input(
            properties = inputSchema["properties"]!!.jsonObject,
            required = inputSchema["required"]!!.jsonArray.map { it.jsonPrimitive.content },
        )
    ) handler@{ (name, arguments, _) ->
        val param = runCatching { Json.decodeFromJsonElement<ParamType>(arguments) }.getOrNull()
            ?: return@handler CallToolResult(content = emptyList(), isError = true)
        val returnValue = runCatching { from(param) }.getOrNull()
            ?: return@handler CallToolResult(content = emptyList(), isError = true)
        val returnString =
            if (returnValue is String) returnValue
            else Json.encodeToString(returnValue)
        CallToolResult(
            content = listOf(TextContent(returnString)),
        )
    }
}