package io.github.stream29.langchain4kt.api.openai.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ContentFilterOffsets(
    @SerialName("check_offset")public  val checkOffset: Int?,
    @SerialName("start_offset")public  val startOffset: Int?,
    @SerialName("end_offset")public  val endOffset: Int?,
)
