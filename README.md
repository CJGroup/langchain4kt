# Langchain4kt2

This library is reimplemented to provide support for the future of LLM.

## Why to remake?

It's a hard decision to remake langchain4kt.

The original idea is to provide a universal and KMP way to use the text-central functionality of LLM. And It faced several issues:

### The difference between LLMs

Different LLMs have very different APIs that sometimes there is no way to unify them. For example, multimodal LLMs from OpenAI and Google Gemini have very different ways to structure the input and output of the model.

### More complexity of the LLMs' input/output types

With the rapid development of LLM technology, the input and output types of LLMs are becoming more and more complex. 

Text, document in certain format, image, audio, video, tool calling, and even more types such as reasoning process, action, parallel tool calling and composed tool calling are involved in the context of LLMs.

Facing this issue, we need a type-safe and easy-to-use way to handle these types. The solution is **union type**. Langchain4kt2 is reimplemented with [KUnion](https://github.com/Stream29/KUnion) to handle these types.

### Need for abstraction and composability

The old OOP style of the original library lacks conciseness and expressiveness of Kotlin, making it more similar to old plain Java code.

The new functional style of Langchain4kt2 allows you to easily compose functionalities and abstract them into reusable components with type-safe and concise code.

## Principles of Langchain4kt2

### Functional style

Functional style make the functionalities more composable with better discoverability and readability.

The core concepts of langchain4kt2 is completely a function:

```kotlin
public typealias Generator<Request, Response> = suspend (Request) -> Response
```

And we have a bunch of extension function to compose the functionalities. 
They are too many to list here. (some are provided in 2 version for suspend and normal function)

### Provide a very similar type-safe API to every model provider, but keep the differences

We have common items for chat history. But the type can be different for some models.

For example, the `OpenAiToolCallRequest` data class is with a `id` field to support the `ChatCompletion` API:

```kotlin
public data class OpenAiToolCallRequest(
    val id: String,
    val name: String,
    val param: String,
)
```

With this, the input/output type of OpenAI API models can be expressed into a union type:

```kotlin
public typealias OpenAiInputContentPartUnion = Union2<
        UserTextMessage,
        UserUrlImageMessage
        >

public typealias OpenAiOutputContentPartUnion = Union2<
        ModelTextMessage,
        ModelUrlImageMessage
        >

public typealias OpenAiHistoryMessageUnion = Union8<
        UserTextMessage,
        SystemTextMessage,
        ModelTextMessage,
        ListInputMessage<OpenAiInputContentPartUnion>,
        ListOutputMessage<OpenAiOutputContentPartUnion>,
        OpenAiToolCallRequest,
        OpenAiToolCallRequestListMessage,
        OpenAiToolCallResultMessage
        >

public typealias OpenAiOutputMessageUnion = Union4<
        ModelTextMessage,
        ListOutputMessage<OpenAiOutputContentPartUnion>,
        OpenAiToolCallRequest,
        OpenAiToolCallRequestListMessage
        >
```

It's up to you to use `ChatCompletion` or `Union`. (See example below)

### Support `kotlinx.serialization` and `kotlinx.coroutine` at best effort

We all need to serialize and deserialize the chat history in some use cases. Langchain4kt2 provide a `Serializable` chat history for every LLM that you can serialize it without information lost.

All the generation API are `suspend fun`, and cooperative with `kotlinx.coroutine`.

The `Union` type is also serializable with `kotlinx.serialization`.

### Reuse existing libraries to make it more usable for now

It's not a good idea to reinvent the wheel. Also, before the support from the community, we need to make the library more usable for more models.

Langchain4kts has bridged APIs from [langchain4j](https://github.com/langchain4j/langchain4j), [openai-kotlin](https://github.com/aallam/openai-kotlin), [Spring AI](https://github.com/spring-projects/spring-ai) and [google-generative-ai-KMP](https://github.com/PatilShreyas/generative-ai-kmp). Thanks for the contributors of these libraries. Developers can use `langchain4kt2` with these libraries to use the models more more simply and typesafe.

Langchain4kt is not going to be only a bridge library. Providing KMP implementations for models is a meaningful thing for the KMP community. But for now, to be realistic, we need to bridge existing libraries first, and implement other model providers' KMP APIs in the future.

## Example

You can use the raw input/output type of the model:

```kotlin
val generate = openAi.asGenerator()
    .configure { model = ModelId("qwen-turbo") }
    .generateByMessages()
    .mapInputFromText()
    .mapOutput { it.singleTextOrNull()!! } // assuming model should return a single text
runBlocking(Dispatchers.IO) {
    val response = generate("hello")
    println(response)
}
```

Or you can use the `mapUnion` to make the type in a serializable union form:

```kotlin
val generateSingle = openAi.asGenerator()
    .configure { model = ModelId("qwen-turbo") }
    .mapUnion()
    .mapInputFromSingle()
val response = generateSingle(SafeUnion8(UserTextMessage("hello")))
response // handling the possible output types of model type-safely
    .consume0 { modelTextMessage -> println("Model: ${modelTextMessage.text}") }
    .consume1 { listOutputMessage -> 
        println("Model:")
        listOutputMessage.list.forEach { item ->
            item.consume0 { modelTextMessage -> println(modelTextMessage.text) }
                .consume1 { modelUrlImageMessage -> println("[image](${modelUrlImageMessage.url})") }
        }
    }.consume2 { toolCallRequest -> println("Model requests to call tool: ${toolCallRequest.name}") }
    .consume3 { toolCallRequestList -> println("Model requests to call tool: ${toolCallRequestList.list.joinToString{ it.name }}") }
```