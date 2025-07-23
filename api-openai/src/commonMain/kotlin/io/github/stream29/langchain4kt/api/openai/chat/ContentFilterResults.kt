package io.github.stream29.langchain4kt.api.openai.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ContentFilterResult(
    public  val filtered: Boolean,
    public  val severity: String,
)

@Serializable
public data class ContentFilterResults(
    public  val hate: ContentFilterResult? = null,
    @SerialName("self_harm")public  val selfHarm: ContentFilterResult? = null,
    public  val sexual: ContentFilterResult? = null,
    public  val violence: ContentFilterResult? = null,
    public  val jailbreak: ContentFilterResult? = null,
    public  val profanity: ContentFilterResult? = null,
)
