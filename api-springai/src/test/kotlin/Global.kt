import io.github.stream29.langchain4kt.api.springai.*
import io.github.stream29.langchain4kt.core.mapOutput
import io.github.stream29.langchain4kt.core.mapOutputFlow
import io.github.stream29.langchain4kt.core.mapSingle
import org.springframework.ai.qianfan.QianFanChatModel
import org.springframework.ai.qianfan.QianFanEmbeddingModel
import org.springframework.ai.qianfan.api.QianFanApi

val apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!
val secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!
val qianFanApi = QianFanApi(apiKey, secretKey)
val qianFanChatModel = QianFanChatModel(qianFanApi)
val generate = qianFanChatModel.asGenerator()
    .generateByMessages()
    .mapInputFromText()
    .mapOutput { it.singleText() }
val streamGenerate = qianFanChatModel.asStreamingGenerator()
    .generateByMessages()
    .mapInputFromText()
    .mapOutputFlow { it.singleText() }
val qianFanEmbeddingModel = QianFanEmbeddingModel(qianFanApi)
val embed = qianFanEmbeddingModel.asEmbeddingGenerator()
    .mapOutput { it.results }
    .mapSingle()
    .mapOutput { it.output }