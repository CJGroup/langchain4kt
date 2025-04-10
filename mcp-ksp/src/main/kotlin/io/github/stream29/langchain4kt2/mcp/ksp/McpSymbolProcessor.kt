package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.stream29.langchain4kt2.mcp.McpServerComponent
import io.github.stream29.langchain4kt2.mcp.McpTool

public class McpSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(McpServerComponent::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { ksClassDeclaration ->
                environment.codeGenerator.createNewFile(
                    dependencies = Dependencies(false),
                    packageName = ksClassDeclaration.packageName.asString(),
                    fileName = "Generated\$${ksClassDeclaration.simpleName.asString()}",
                    extensionName = "kt"
                ).bufferedWriter().use { file ->
                    val toolFuncNameList = ksClassDeclaration.getAllFunctions()
                        .filter { func -> func.annotations.any { it.annotationType.qualifiedName() == McpTool::class.qualifiedName } }
                        .map { func -> func.simpleName.asString() }
                    file.write(
                        buildString {
                            appendLine("package ${ksClassDeclaration.packageName.asString()}\n")
                            appendLine("// This is a generated file. Do not edit it directly.")
                            appendLine("import io.github.stream29.langchain4kt2.mcp.ServerAdapter")
                            appendLine("import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool")
                            appendLine("import io.github.stream29.langchain4kt2.mcp.makeTool")
                            appendLine("public fun ${ksClassDeclaration.simpleName.asString()}.tools(adapter: ServerAdapter = ServerAdapter.default): List<RegisteredTool> {")
                            appendLine("    return listOf(")
                            toolFuncNameList.forEach { funcName ->
                                appendLine("        adapter.makeTool(\"$funcName\", null, this::$funcName),")
                            }
                            appendLine("    )")
                            appendLine("}")
                        }
                    )
                }
            }
        return emptyList()
    }
}