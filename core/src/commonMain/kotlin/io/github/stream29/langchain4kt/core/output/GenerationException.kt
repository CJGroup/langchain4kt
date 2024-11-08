package io.github.stream29.langchain4kt.core.output

public class GenerationException : Exception {
    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Exception) : super(message, cause)
}