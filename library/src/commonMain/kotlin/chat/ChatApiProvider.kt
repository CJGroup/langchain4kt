package chat

import chat.input.IContext
import chat.output.Response

interface IChatApiProvider {
    fun generate(context: IContext): Response
}