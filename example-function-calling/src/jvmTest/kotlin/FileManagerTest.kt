import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.example.functioncalling.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test

class FileManagerTest {
    @Test
    fun fileInfoSearch() {
        val model = FunctionCallingModel(
            apiProvider = qianfanApiProvider,
            memoryMetaprompt = ::memoryMetaprompt,
            resolveFunctionCall = ::resolveFunctionCall,
            onFunctionReturn = ::onFunctionReturn,
            functions = metaFunction,
            context = Context.of { systemInstruction(functionCallPrompt(metaFunction)) },
            finalResponsePrompt = { "$it\n请据此对我进行回复。" },
            memory = "我的桌面位于C:\\Users\\Lenovo\\Desktop"
        )
        val result = runBlocking {
            model.chat("我的桌面上有特别大的文件夹吗？")
        }
        println(result)
    }
}

val metaFunction = listOf(
    GptFunction {
        name("发起文件查询")
        addParam("指示内容", "对文件查询的指示，由一个或多个短句组成，并且指明需要返回的内容")
        description("发起一次文件查询，返回查询结果的汇报。文件查询可以包含以下几种命令：查询文件大小、查询文件夹大小、查询路径下内容")
        resolveWith { prompt ->
            val respondent = FunctionCallingRespondent(
                apiProvider = qianfanApiProvider,
                resolveFunctionCall = ::resolveFunctionCall,
                onFunctionReturn = ::onFunctionReturn,
                functions = fileFunction,
                systemInstruction = functionCallPrompt(fileFunction) {
                    appendLine("所有文件查询都可以且必须通过函数调用完成。")
                    appendLine("对于每个查询，你都必须提供完整的结果，不可以有省略。")
                },
                messageModifier = { "$it\n如果你接下来需要调用函数，请先说明你调用的目的再进行调用；如果已经不需要再调用函数了，请根据所有信息给出最终的完整回复" }
            )
            respondent.chat(prompt[0])
        }
    }.exampleExplained("找出E:\\ACodeSpace目录下的所有文件夹，查询每一个的大小，并筛选出其中大小大于1MB的文件夹") { "找到2个文件夹：\nIDEA\nVSCode" },
)

val fileFunction = listOf(
    GptFunction {
        name("运行kotlin代码")
        addParam("代码", "一段kotlin代码")
        description("返回值为脚本的输出内容。你可以通过这个函数来执行文件操作。")
        resolveWith { args ->
            val script = args.first()
            captureOut {
                jsr223KtsEngine.eval("$script\nmain()")
            }
        }
    }.exampleExplained(
        """
        import java.io.File

        fun main() {
            println(File("C:\\").listFiles()!!.joinToString("\n") { it.name })
        }
    """.trimIndent())
)

private fun captureOut(body: () -> Unit): String {
    val outStream = ByteArrayOutputStream()
    val prevOut = System.out
    System.setOut(PrintStream(outStream))
    try {
        body()
    } finally {
        System.out.flush()
        System.setOut(prevOut)
    }
    return outStream.toString().trim()
}