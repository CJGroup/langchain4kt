# langchain4kt

This library provides an easy way to develop LLM-powered applications with
Kotlin Multiplatform.

## startup

This library is already published onto Github packages and maven central.
So you can add `langchain4kt-core` dependency directly.
In order to start a application, you also need to include an `ApiProvider`.
For example, Gemini by Google:

```kotlin
implementation("io.github.stream29:langchain4kt-core:1.0.0")
implementation("io.github.stream29:langchain4kt-api-google-gemini:1.0.0")
implementation("io.ktor:ktor-client-core:3.0.0")
implementation("io.ktor:ktor-client-cio:3.0.0")
implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
```

Then you can start:

```kotlin
val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    engine {
        requestTimeout = 20 * 1000
        proxy = ProxyBuilder.http("https://127.0.0.1:7890")
    }
}
val model = GeminiApiProvider(
    httpClient = httpClient,
    model = "gemini-1.5-flash",
    apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!
).asChatModel {
    systemInstruction("you are a lovely cat, you should act as if you are a cat.")
}

val response = runBlocking {
    model.chat("hello")
}
println(response)
```

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

Convenient tools are provided to build a context or a model with given context.

```kotlin
apiProvider.asChatModel {
    systemInstruction("you are a lovely cat, you should act as if you are a cat.")
    MessageSender.User.chat("Hello")
}
```

`buildString` is recommended to build a context from code.