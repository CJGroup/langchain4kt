package chat

import chat.input.IContext
import chat.message.Message
import chat.output.Response

interface IChatLanguageModel {
    val context: IContext
    fun chat(message: Message): Response
}

class ChatLanguageModel(
    override val context: IContext,
    var apiProvider: IChatApiProvider
) : IChatLanguageModel {
    override fun chat(message: Message): Response {
        context.history.add(message)
        val response = apiProvider.generate(context)
        context.history.add(response)
        return response
    }
}