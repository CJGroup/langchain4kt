package io.github.stream29.langchain4kt.api.baiduqianfan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("session_key")
    val sessionKey: String,
    @SerialName("access_token")
    val accessToken: String,
    val scope: String,
    @SerialName("session_secret")
    val sessionSecret: String
)

@Serializable
data class AccessTokenError(
    @SerialName("error_description")
    val errorDescription: String,
    val error: String
)

@Serializable
data class RequestError(
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_msg")
    val errorMsg: String
)

//{
//    "id": "as-fg4g836x8n",
//    "object": "chat.completion",
//    "created": 1709716601,
//    "result": "北京，简称“京”，古称燕京、北平，中华民族的发祥地之一，是中华人民共和国首都、直辖市、国家中心城市、超大城市，也是国务院批复确定的中国政治中心、文化中心、国际交往中心、科技创新中心，中国历史文化名城和古都之一，世界一线城市。\n\n北京被世界城市研究机构评为世界一线城市，联合国报告指出北京市人类发展指数居中国城市第二位。北京市成功举办夏奥会与冬奥会，成为全世界第一个“双奥之城”。北京有着3000余年的建城史和850余年的建都史，是全球拥有世界遗产（7处）最多的城市。\n\n北京是一个充满活力和创新精神的城市，也是中国传统文化与现代文明的交汇点。在这里，你可以看到古老的四合院、传统的胡同、雄伟的长城和现代化的高楼大厦交相辉映。此外，北京还拥有丰富的美食文化，如烤鸭、炸酱面等，以及各种传统艺术表演，如京剧、相声等。\n\n总的来说，北京是一个充满魅力和活力的城市，无论你是历史爱好者、美食家还是现代都市人，都能在这里找到属于自己的乐趣和归属感。",
//    "is_truncated": false,
//    "need_clear_history": false,
//    "finish_reason": "normal",
//    "usage": {
//    "prompt_tokens": 2,
//    "completion_tokens": 221,
//    "total_tokens": 223
//}
//}
@Serializable
data class QianfanChatResponse(
    val id: String,
    val `object`: String,
    val created: Int,
    @SerialName("sentence_id")
    val sentenceId: String,
    @SerialName("is_end")
    val isEnd: Boolean,
    @SerialName("is_truncated")
    val isTruncated: Boolean,
    @SerialName("finish_reason")
    val finishReason: String,
    @SerialName("search_info")
    val searchInfo: SearchInfo,
    val result: String,
    @SerialName("need_clear_history")
    val needClearHistory: Boolean,
    @SerialName("flag")
    val flag: String,
    @SerialName("ban_round")
    val banRound: Boolean,
    val usage: Usage
)

@Serializable
data class SearchInfo(
    @SerialName("search_results")
    val searchResults: List<SearchResult>
)

@Serializable
data class SearchResult(
    val index: Int,
    val url: String,
    val title: String
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)