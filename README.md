# langchain4kt

This library provides an easy way to develop LLM-powered applications with
Kotlin Multiplatform.

## startup

This library is already published onto Github packages and maven central.
So you can add `langchain4kt-core` dependency directly.

To start up, you can refer to [kotlin notebook example](example-kotlin-notebook/BasicUsage.ipynb)
or [chain-of-thought example](example-chain-of-thought/src/jvmTest) [file agent example](example-function-calling/src/jvmTest).

## for development

The core of langchain4kt is consisted by three interfaces.

```kotlin
/**
 * An LLM api provider that generates a text response based on the given context.
 *
 * It should be stateless and concurrency-safe.
 *
 * @param MetaInfo The type of the meta info that the api provider generates.
 */
public interface ChatApiProvider<MetaInfo> {
    /**
     * Generates a text response with meta info based on the given context.
     */
    public suspend fun generate(context: Context): Response<MetaInfo>
}
```

```kotlin
/**
 * A respondent that generate a single response for a single message.
 *
 * It should be stateless and concurrency-safe.
 */
public interface Respondent {
    /**
     * Generate a response for the given message.
     */
    public suspend fun chat(message: String): String
}
```

```kotlin
/**
 * A chat model that records its own historical [context].
 *
 * You can simply chat with [String] message. Every message will produce a response in [String].
 *
 * It should provide **strong exception safety guarantee** that when [chat] throws a exception, the state of itself **should not** change
 *
 * It is **not** concurrency safe.
 */
public interface ChatModel {
    public val context: Context
    public suspend fun chat(message: String): String
}
```

You can implement the interface to develop an AI-agent, or something more.
See into examples in the repository.

Streaming version of the interfaces are also provided in `langchain4kt-streaming` module.

Convenient tools are provided to build a context or a model with given context.

```kotlin
apiProvider.asChatModel {
    systemInstruction("you are a lovely cat, you should act as if you are a cat.")
    MessageSender.User.chat("Hello")
}
```

`buildString` is recommended to build a context from code.

## Future Plan

multimodal I/O, tool calling, structured output are planned to be supported in the future.

priority from high to low:

structured output
tool calling
multimodal I/O