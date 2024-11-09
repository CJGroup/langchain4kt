import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.example.functioncalling.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test

class GptGptFunctionCallingTest {
    @Test
    fun simpleFunctionCall() {
        val functions = listOf(
            GptFunction {
                name("查询现在时间")
                description("返回现在的时间")
                resolveWith { _ -> LocalDateTime.now().toString() }
            }.exampleExplained(),
        )
        val model = FunctionCallingModel(
            apiProvider = qianfanApiProvider,
            memoryMetaprompt = ::memoryMetaprompt,
            resolveFunctionCall = ::resolveFunctionCall,
            onFunctionReturn = ::onFunctionReturn,
            functions = functions,
            context = Context.of { systemInstruction(functionCallPrompt(functions)) },
            finalResponsePrompt = { "$it\n请据此对我进行回复。" }
        )
        runBlocking {
            model.chat("现在几点？")
        }
        println(model.memory)
        println(model.context.history)
    }
}

const val stopSequence = "======"