package io.github.stream29.langchain4kt2.core

/**
 * Exception that is thrown when generation of a response fails.
 */
public class GenerationException : Exception {
    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}