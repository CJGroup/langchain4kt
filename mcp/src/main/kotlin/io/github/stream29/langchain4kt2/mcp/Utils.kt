package io.github.stream29.langchain4kt2.mcp

import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent

@PublishedApi
internal fun CallToolResult.Companion.ok(content: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(content)),
        isError = false,
    )

@PublishedApi
internal fun CallToolResult.Companion.error(content: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(content)),
        isError = true,
    )