package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.output.Response

interface ChatLanguageModel<SuccessInfo, FailInfo> {
    val context: Context
    suspend fun <Content> chat(message: Message<Content>): Response<Message<*>, SuccessInfo, FailInfo>
}

class SimpleChatLanguageModel<SuccessInfo, FailInfo>(
    override val context: Context,
    var apiProvider: ChatApiProvider<SuccessInfo, FailInfo>
) : ChatLanguageModel<SuccessInfo, FailInfo> {
    override suspend fun <Content> chat(message: Message<Content>): Response<Message<*>, SuccessInfo, FailInfo> {
        context.history.add(message)
        val response = apiProvider.generate(context)
        if (response is Response.Success)
            context.history.add(response.content)
        else
            context.history.removeLast()
        return response
    }
}