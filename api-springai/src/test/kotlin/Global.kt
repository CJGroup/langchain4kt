import io.github.stream29.langchain4kt.api.springai.asApiProvider
import io.github.stream29.langchain4kt.api.springai.asEmbeddingGenerator
import io.github.stream29.langchain4kt.api.springai.asStreamingApiProvider
import org.springframework.ai.qianfan.QianFanChatModel
import org.springframework.ai.qianfan.QianFanEmbeddingModel
import org.springframework.ai.qianfan.api.QianFanApi

val apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!
val secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!
val qianFanApi = QianFanApi(apiKey, secretKey)
val qianFanChatModel = QianFanChatModel(qianFanApi)
val chatApiProvider = qianFanChatModel.asApiProvider()
val streamChatApiProvider = qianFanChatModel.asStreamingApiProvider()
val qianFanEmbeddingModel = QianFanEmbeddingModel(qianFanApi)
val embeddingApiProvider = qianFanEmbeddingModel.asEmbeddingGenerator()