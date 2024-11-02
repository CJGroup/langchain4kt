package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.Respondent

data class MetapromptRespondent(
    val baseRespondent: Respondent,
    val promptgenRespondent: Respondent = baseRespondent,
    val metaprompt: (String) -> String
) : Respondent {
    override suspend fun chat(message: String): String {
        val prompt = promptgenRespondent.chat(metaprompt(message))
        return baseRespondent.chat(prompt)
    }
}