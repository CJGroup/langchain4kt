package chat

import chat.input.Context
import chat.output.Response

interface ChatApiProvider {
    fun generate(context: Context): Response
}