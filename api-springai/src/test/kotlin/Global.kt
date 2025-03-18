import io.github.stream29.langchain4kt2.core.mapOutput
import io.github.stream29.langchain4kt2.core.mapOutputFlow
import io.github.stream29.langchain4kt2.core.mapSingle
import io.github.stream29.langchain4kt2.api.springai.asEmbeddingGenerator
import io.github.stream29.langchain4kt2.api.springai.asGenerator
import io.github.stream29.langchain4kt2.api.springai.asStreamingGenerator
import io.github.stream29.langchain4kt2.api.springai.generateByMessages
import io.github.stream29.langchain4kt2.api.springai.mapInputFromText
import io.github.stream29.langchain4kt2.api.springai.singleText
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