package chat

import chat.input.Context
import chat.message.Message
import chat.output.Response

interface IChatLanguageModel<SuccessInfo, FailInfo> {
    val context: Context
    fun <Content> chat(message: Message<Content>): Response<*, SuccessInfo, FailInfo>
}

class ChatLanguageModel<SuccessInfo, FailInfo>(
    override val context: Context,
    var apiProvider: IChatApiProvider<SuccessInfo, FailInfo>
) : IChatLanguageModel<SuccessInfo, FailInfo> {
    override fun <Content> chat(message: Message<Content>): Response<*, SuccessInfo, FailInfo> {
        context.history.add(message)
        val response = apiProvider.generate(context)
        if (response is Response.Success)
            context.history.add(response.message)
        else
            context.history.removeLast()
        return response
    }
}