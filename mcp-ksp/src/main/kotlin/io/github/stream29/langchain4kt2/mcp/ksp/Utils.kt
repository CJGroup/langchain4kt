package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.KSTypeReference

public fun KSTypeReference.qualifiedName(): String? = this.resolve().declaration.qualifiedName?.asString()