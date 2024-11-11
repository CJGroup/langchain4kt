import io.github.stream29.langchain4kt.example.functioncalling.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test

class GptFunctionCallingTest {
    @Test
    fun simpleFunctionCall() {
        val functions = listOf(
            GptFunction {
                name("查询现在时间")
                description("返回现在的时间")
                resolveWith { _ -> LocalDateTime.now().toString() }
            }.exampleExplained(),
        )
        val respondent = FunctionCallingRespondent(
            apiProvider = qianfanApiProvider,
            resolveFunctionCall = ::resolveFunctionCall,
            onFunctionReturn = ::onFunctionReturn,
            functions = functions,
            systemInstruction = functionCallPrompt(functions),
            messageModifier = { "$it\n如果你接下来需要调用函数，请先说明你调用的目的再进行调用；如果已经不需要再调用函数了，请根据所有信息给出最终的完整回复" }
        )
        val result = runBlocking {
            respondent.chat("现在几点？")
        }
        println(result)
    }
}