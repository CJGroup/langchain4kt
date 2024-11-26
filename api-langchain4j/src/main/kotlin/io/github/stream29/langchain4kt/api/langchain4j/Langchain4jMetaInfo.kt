package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.model.output.FinishReason
import dev.langchain4j.model.output.Response
import dev.langchain4j.model.output.TokenUsage

/**
 * Meta information of [Response].
 */
public data class Langchain4jMetaInfo(
    val tokenUsage: TokenUsage,
    val finishReason: FinishReason,
    val metadata: Map<String, Any>,
) {
    public constructor(response: Response<*>): this(
        tokenUsage = response.tokenUsage()!!,
        finishReason = response.finishReason()!!,
        metadata = response.metadata()!!,
    )
}