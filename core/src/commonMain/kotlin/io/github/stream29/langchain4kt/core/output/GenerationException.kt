package io.github.stream29.langchain4kt.core.output

class GenerationException(
    message: String,
    cause: Exception
) : Exception(message, cause)