import io.github.stream29.langchain4kt.core.asChatModel
import io.github.stream29.langchain4kt.core.asRespondent
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CotTest {
    @Test
    fun respondentTest() {
        val respondent = apiProvider.asRespondent(cotPrompt)
        runBlocking {
            println(respondent.chat("请分析两个球撞击的不同情况"))
        }
    }

    @Test
    fun modelTest() {
        val model = apiProvider.asChatModel {
            systemInstruction(cotPrompt)
        }
        runBlocking {
            model.chat("e^(x^2)的积分如何计算？")
            model.chat("请更详细地解释你刚刚的回答背后的数学原理。")
            println(model.context.history.joinToString("\n") { "${it.sender}: \n${it.content}" })
        }
    }
}