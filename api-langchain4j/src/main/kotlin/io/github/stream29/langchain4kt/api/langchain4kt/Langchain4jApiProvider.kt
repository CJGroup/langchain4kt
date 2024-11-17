package io.github.stream29.langchain4kt.api.langchain4kt

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.asStreamChatModel
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.Response

/**
 * Wrapping [ChatLanguageModel] to [ChatApiProvider].
 */
public data class Langchain4jApiProvider(
    val model: ChatLanguageModel,
) : ChatApiProvider<Langchain4jMetaInfo> {
    override suspend fun generate(context: Context): Response<Langchain4jMetaInfo> {
        val aiMessageResponse = model.generate(
            buildList {
                context.systemInstruction?.let {
                    add(SystemMessage(it))
                }
                context.history.forEach {
                    when (it.sender) {
                        MessageSender.User -> add(UserMessage(it.content))
                        MessageSender.Model -> add(AiMessage(it.content))
                    }
                }
            }
        )
        return Response(
            message = aiMessageResponse.content().text()!!,
            metaInfo = Langchain4jMetaInfo(aiMessageResponse)
        )
    }
}