package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.output.Response

interface ChatModel<SuccessInfo, FailInfo> {
    val context: Context
    suspend fun chat(message: Message): Response<Message, SuccessInfo, FailInfo>
}

class SimpleChatModel<SuccessInfo, FailInfo>(
    override val context: Context,
    var apiProvider: ChatApiProvider<SuccessInfo, FailInfo>
) : ChatModel<SuccessInfo, FailInfo> {
    override suspend fun chat(message: Message): Response<Message, SuccessInfo, FailInfo> {
        context.history.add(message)
        val response = apiProvider.generate(context)
        if (response is Response.Success)
            context.history.add(response.content)
        else
            context.history.removeLast()
        return response
    }
}