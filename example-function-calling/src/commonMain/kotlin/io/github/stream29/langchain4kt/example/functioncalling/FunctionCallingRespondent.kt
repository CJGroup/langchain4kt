package io.github.stream29.langchain4kt.example.functioncalling

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.Respondent
import io.github.stream29.langchain4kt.core.asChatModel
import kotlinx.coroutines.coroutineScope

data class FunctionCallingRespondent(
    val apiProvider: ChatApiProvider<*>,
    val systemInstruction: String,
    val functions: List<GptFunctionExample>,
    val resolveFunctionCall: (String) -> List<GptFunctionCall>,
    val onFunctionReturn: (List<Result<GptFunctionResult>>) -> String,
    val messageModifier: (originalMessage: String) -> String,
) : Respondent {
    override suspend fun chat(message: String): String {
        val simpleModel = apiProvider.asChatModel { systemInstruction(systemInstruction) }
        suspend fun chatRecursively(message: String): String = coroutineScope {
            val response = simpleModel.chat(messageModifier(message))
            val functionCalls = resolveFunctionCall(response)
            if (functionCalls.isEmpty()) {
                return@coroutineScope response
            }
            val functionResults = functionCalls.map { functionCall ->
                runCatching {
                    functions.find { it.function.name == functionCall.functionName }
                        ?.function
                        ?.invoke(functionCall.params)
                        ?: throw FunctionNotFoundException(functionCall)
                }
            }
            val functionReturn = onFunctionReturn(functionResults)
            return@coroutineScope chatRecursively(functionReturn)
        }
        return chatRecursively(message)
    }
}