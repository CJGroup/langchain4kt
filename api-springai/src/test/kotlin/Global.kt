import io.github.stream29.langchain4kt.api.springai.SpringAiChatApiProvider
import io.github.stream29.langchain4kt.api.springai.SpringAiStreamChatApiProvider
import io.github.stream29.langchain4kt.api.springai.asLangchain4ktProvider
import org.springframework.ai.qianfan.QianFanChatModel
import org.springframework.ai.qianfan.QianFanEmbeddingModel
import org.springframework.ai.qianfan.api.QianFanApi

val apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!
val secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!
val qianFanApi = QianFanApi(apiKey, secretKey)
val qianFanChatModel = QianFanChatModel(qianFanApi)
val chatApiProvider = SpringAiChatApiProvider(qianFanChatModel)
val streamChatApiProvider = SpringAiStreamChatApiProvider(qianFanChatModel)
val qianFanEmbeddingModel = QianFanEmbeddingModel(qianFanApi)
val embeddingApiProvider = qianFanEmbeddingModel.asLangchain4ktProvider()