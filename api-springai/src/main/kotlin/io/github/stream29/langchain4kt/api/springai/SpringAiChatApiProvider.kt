package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Model

public data class SpringAiChatApiProvider(
    val chatModel: Model<Prompt, ChatResponse>
) : ChatApiProvider<ChatResponse> {
    override suspend fun generate(context: Context): Response<ChatResponse> {
        val messageList = fromContext(context)
        val chatResponse = chatModel.call(Prompt(messageList))
        return Response(
            message = chatResponse.result.output.content,
            metaInfo = chatResponse
        )
    }
}