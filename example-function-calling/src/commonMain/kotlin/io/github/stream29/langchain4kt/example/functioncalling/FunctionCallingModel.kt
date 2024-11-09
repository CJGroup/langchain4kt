package io.github.stream29.langchain4kt.example.functioncalling

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.asRespondent
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.GenerationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class FunctionCallingModel(
    val apiProvider: ChatApiProvider<*>,
    val memoryMetaprompt: (String, FunctionCallingMessage) -> String,
    val resolveFunctionCall: (String) -> List<GptFunctionCall>,
    val onFunctionReturn: (List<Result<GptFunctionResult>>) -> String,
    val finalResponsePrompt: (memory: String) -> String,
    val functions: List<GptFunctionExample>,
    var memory: String = "",
    override val context: Context = Context()
) : ChatModel {
    private val respondent = apiProvider.asRespondent(context.systemInstruction)
    private val memoryRespondent = apiProvider.asRespondent()
    override suspend fun chat(message: String): String = coroutineScope {
        val historyLengthBackup = context.history.size
        val memoryBackup = memory
        try {
            context.add { MessageSender.User.chat(message) }
            onMessage(message, FunctionCallingMessageType.UserMessage)
            val response = respondent.chat(memory)
            val functionCalls = resolveFunctionCall(response)

            if (functionCalls.isEmpty()) {
                onMessage(response, FunctionCallingMessageType.ModelMessage)
                context.add { MessageSender.Model.chat(response) }
                return@coroutineScope response
            }
            val addFunctionCallingMessage = launch {
                onMessage(response, FunctionCallingMessageType.FunctionCall)
            }
            val functionResults = functionCalls.map { functionCall ->
                async {
                    runCatching {
                        functions.find { it.function.name == functionCall.functionName }
                            ?.function
                            ?.invoke(functionCall.params)
                            ?: throw FunctionNotFoundException(functionCall)
                    }
                }
            }.awaitAll()
            addFunctionCallingMessage.join()
            val functionReturn = onFunctionReturn(functionResults)
            onMessage(functionReturn, FunctionCallingMessageType.FunctionReturn)

            val finalResponse = memoryRespondent.chat(finalResponsePrompt(memory))
            onMessage(finalResponse, FunctionCallingMessageType.ModelMessage)
            context.add { MessageSender.Model.chat(finalResponse) }
            finalResponse
        } catch (e: Exception) {
            while (context.history.size > historyLengthBackup) {
                context.history.removeLast()
            }
            memory = memoryBackup
            throw GenerationException("Generation failed with prompt $message", e)
        }
    }

    private suspend fun onMessage(message: String, messageType: FunctionCallingMessageType) {
        val prompt = memoryMetaprompt(
            memory,
            FunctionCallingMessage(
                messageType,
                message
            )
        )
        memory = memoryRespondent.chat(prompt)
    }
}