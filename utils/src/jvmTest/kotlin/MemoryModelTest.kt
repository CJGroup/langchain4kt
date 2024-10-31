import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import io.github.stream29.langchain4kt.core.generate
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.utils.IteratedPromptModel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class MemoryModelTest {
    @Test
    fun qianfanTest() {
        testMemoryWith(qianfanApiProvider)
    }

    @Test
    fun geminiTest() {
        testMemoryWith(geminiApiProvider)
    }
}

fun testMemoryWith(apiProvider: ChatApiProvider<*>) {
    val model = IteratedPromptModel(
        baseModel = apiProvider.asChatModel()
    ) { message, oldPrompt ->
        runBlocking {
            val senderName = if (message.sender == MessageSender.User) "我" else "你"
            qianfanApiProvider.generate(
                """
                    ##### 以下为历史记忆 #####
                    $oldPrompt
                    ##### 以上为历史记忆 #####
                    
                    ##### 以下为新加入的信息 #####
                    ##### ${senderName}说： #####
                    ${message.content}
                    ##### 以上为${senderName}说的内容 #####
                    ##### 以上为新加入的信息 #####
                    
                    这是一段记忆，请从这段记忆中总结有用的记忆并将新加入的信息加入历史记忆中。
                    你应当遵守输出格式：
                    以“我”或“你”为主语将历史信息整理成一系列句子，并且整理句意，删去细枝末节的内容。
                    在整合新加入的信息时，应当将说的内容整理成包含意图与内容的信息。
                    注意保留“我”的信息和意图，同时保留“你”的回复主要内容。不要保留“我说”或者“你说”。
                    
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
                """.trimIndent()
            )
        }
    }
    runBlocking {
        model.chat("你好，我对人文哲学感兴趣，可以介绍一些这方面的大师吗？")
        model.chat("我想了解你对他们的看法。")
    }
}