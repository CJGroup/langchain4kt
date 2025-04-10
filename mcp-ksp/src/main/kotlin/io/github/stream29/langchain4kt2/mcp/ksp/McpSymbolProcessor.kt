package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.stream29.langchain4kt2.mcp.McpServerComponent

public class McpSymbolProcessor(private val environment: SymbolProcessorEnvironment): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(McpServerComponent::class.qualifiedName!!).filterIsInstance<KSClassDeclaration>().forEach {
            environment.codeGenerator.createNewFile(
                dependencies = Dependencies(false),
                packageName = it.packageName.asString(),
                fileName = "Generated\$${it.simpleName.asString()}",
                extensionName = "kt"
            ).bufferedWriter().use { file ->
                file.write("// This file is generated. Do not edit.\n")
            }
        }
        return emptyList()
    }
}