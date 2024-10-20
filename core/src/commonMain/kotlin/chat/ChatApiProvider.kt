package chat

import chat.input.Context
import chat.output.Response

interface IChatApiProvider<SuccessInfo, FailInfo> {
    suspend fun generate(context: Context): Response<*, SuccessInfo, FailInfo>
}