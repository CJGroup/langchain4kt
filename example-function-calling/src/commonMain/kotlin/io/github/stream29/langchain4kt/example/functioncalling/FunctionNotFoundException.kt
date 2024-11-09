package io.github.stream29.langchain4kt.example.functioncalling

class FunctionNotFoundException(
    val call: GptFunctionCall
) : Exception("Function not found: ${call.functionName}")
