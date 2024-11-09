package io.github.stream29.langchain4kt.example.functioncalling

import com.javiersc.kotlinx.coroutines.run.blocking.runBlocking

@GptFunctionDsl
fun GptFunctionBuilder.name(name: String) = apply { this.name = name }

@GptFunctionDsl
fun GptFunctionBuilder.addParam(name: String, description: String) =
    apply { params.add(GptParameter(name, description)) }

@GptFunctionDsl
fun GptFunctionBuilder.description(description: String) = apply { this.description = description }

@GptFunctionDsl
fun GptFunctionBuilder.resolveWith(body: suspend (List<String>) -> String) = apply { this.resolve = body }

@GptFunctionDsl
fun GptFunction(builder: GptFunctionBuilder.() -> Unit): GptFunction {
    val apply = GptFunctionBuilder().apply(builder)
    return GptFunction(
        name = requireNotNull(apply.name),
        params = apply.params,
        description = requireNotNull(apply.description),
        resolve = requireNotNull(apply.resolve)
    )
}

@GptFunctionDsl
fun GptFunction.exampleExplained(vararg params: String) =
    GptFunctionExample(this, params.toList(), runBlocking { resolve(params.toList()) })

@GptFunctionDsl
fun GptFunction.exampleExplained(vararg params: String, result: (List<String>) -> String) =
    GptFunctionExample(this, params.toList(), result(params.toList()))

data class GptFunction(
    val name: String,
    val params: List<GptParameter>,
    val description: String,
    val resolve: suspend (List<String>) -> String
) : suspend (List<String>) -> GptFunctionResult {
    override suspend operator fun invoke(params: List<String>) = GptFunctionResult(name, params, resolve(params))
}

data class GptFunctionExample(
    val function: GptFunction,
    val params: List<String>,
    val result: String
)

data class GptFunctionCall(
    val functionName: String,
    val params: List<String>
)

data class GptFunctionResult(
    val functionName: String,
    val params: List<String>,
    val result: String
)

data class GptParameter(
    val name: String,
    val description: String
)

data class GptFunctionBuilder(
    internal var name: String? = null,
    internal var params: MutableList<GptParameter> = mutableListOf(),
    internal var description: String? = null,
    internal var resolve: (suspend (List<String>) -> String)? = null
)
