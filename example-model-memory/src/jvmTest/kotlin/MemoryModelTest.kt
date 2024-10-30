import io.github.stream29.langchain4kt.api.baiduqianfan.GenerateConfig
import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.core.SimpleChatModel
import io.github.stream29.langchain4kt.core.generate
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.example.memory.IteratedPromptModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class MemoryModelTest {
    @Test
    fun generationTest() {
        val apiProvider = QianfanApiProvider(
            httpClient = httpClient,
            apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
            secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
            model = "ernie-4.0-8k-latest",
            generateConfig = GenerateConfig(),
        )
        val model = IteratedPromptModel(
            baseModel = SimpleChatModel(
                apiProvider = apiProvider
            )
        ) { message, oldPrompt ->
            runBlocking {
                val senderName = if(message.sender == MessageSender.User) "我" else "你"
                apiProvider.generate("""
                    $oldPrompt
                    ${senderName}说：${message.content}
                    这是一段历史提示词，请从这段提示词中提取有用的历史信息。
                    你应当遵守输出格式：
                    以“我”或“你”为主语将历史信息整理成一系列句子，并且整理句意，删去不重要的内容。
                    注意保留“我”的信息和意图，同时保留“你”的回复主要内容。
                    
                    例如：
                    历史提示词：
                    “太阳从东边升起。我是一个学生，只了解高中数学。
                    我希望你为我讲解微积分的入门知识。
                    你从微积分的概念、微积分的使用、微积分的应用等方面为我讲解。
                    我说：请编写几道例题”
                    你的输出：
                    “我是一个学生，只了解高中数学。
                    我希望你为我讲解微积分的入门知识。
                    你从微积分的概念、微积分的使用、微积分的应用等方面为我讲解。
                    我希望你编写几道例题”
                    你只需要输出例子中的“你的输出”部分。也就是引号内的内容，不包含引号。
                """.trimIndent())
            }
        }
        runBlocking{
            model.chat("你好，我对人文哲学感兴趣，可以介绍一些这方面的大师吗？")
            model.chat("我想了解你对他们的看法。")
        }
    }
}