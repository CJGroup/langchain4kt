package io.github.stream29.langchain4kt.example.functioncalling

import io.github.stream29.langchain4kt.core.ChatModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

data class FunctionCallingModel(
    val baseModel: ChatModel,
    val resolveFunctionCall: (String) -> List<GptFunctionCall>,
    val onFunctionCall: suspend (GptFunctionCall) -> GptFunctionResult,
    val onFunctionReturn: (GptFunctionResult) -> String
) : ChatModel by baseModel {
    override suspend fun chat(message: String): String {
        val response = baseModel.chat(message)
        val functionCall = resolveFunctionCall(response)
        if (functionCall.isEmpty()) return response
        val functionReturns = coroutineScope {
            functionCall.asFlow().map {
                async {
                    onFunctionReturn(onFunctionCall(it))
                }
            }.buffer().toList().awaitAll().joinToString("\n")
        }
        val functionCallResponse = baseModel.chat(functionReturns)
        return functionCallResponse
    }
}

@Suppress("function_name")
fun FunctionCallingModel(baseModel: ChatModel, functionExplained: List<GptFunctionExample>): FunctionCallingModel {
    val functionMap = functionExplained.associateBy { it.function.name }
    return FunctionCallingModel(
        baseModel,
        resolveFunctionCall = ::resolveFunctionCall,
        onFunctionCall = {
            val function = functionMap[it.functionName]
            if (function == null) {
                GptFunctionResult(it.functionName, it.params, "警告：尝试调用不存在的函数")
            } else {
                val result = function.function.resolve.invoke(it.params)
                GptFunctionResult(function.function.name, it.params, result)
            }
        },
        onFunctionReturn = {
            """
            =====函数调用结果=====
            函数：${it.functionName}
            调用参数：${it.params}
            返回值：${it.result}
            =====函数调用结果=====
        """.trimIndent()
        }
    )
}

private fun resolveFunctionCall(message: String): List<GptFunctionCall> {
    return message.substringAfter("=====FUNCTION=CALL=====")
        .lines()
        .asSequence()
        .map {
            if (!it.startsWith("==="))
                return@map null
            val functionCallInfo = it.substringAfter("===").split("=")
            val functionName = functionCallInfo.first()
            val params = functionCallInfo.drop(1)
            if(functionName.isEmpty())
                return@map null
            GptFunctionCall(
                functionName,
                params
            )
        }.filterNotNull().toList()
}

const val stopSequence = "======"