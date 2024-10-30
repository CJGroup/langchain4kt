import io.github.stream29.langchain4kt.api.baiduqianfan.GenerateConfig
import io.github.stream29.langchain4kt.example.memory.MetapromptChatModel
import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class MetapromptTest {
    @Test
    fun generationTest() {
        val apiProvider = QianfanApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
            secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
            model = "ernie-4.0-8k-latest",
            generateConfig = GenerateConfig(),
        )
        val model = MetapromptChatModel(
            apiProvider = apiProvider,
        ) { prompt ->
            """
                $prompt
                ##### 以上为输入 #####
                
                你是一个撰写GPT提示词的专家，你需要生成一段提示词，来引导GPT进行回复。
                请写一段提示词，来引导一个语言模型回复输入的内容。
                这段提示词应当满足以下条件：
                阐述问题的背景，阐述输入的意图，引导模型回答问题，并且指出回答应当满足什么要求。
                不要输出除了提示词以外的任何内容。
                """.trimIndent()
        }
        runBlocking {
            model.chat("一个初学者应该如何入门微积分呢？")
        }
    }
}