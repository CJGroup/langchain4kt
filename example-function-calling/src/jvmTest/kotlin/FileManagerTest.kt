import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.example.functioncalling.*
import kotlinx.coroutines.runBlocking
import java.io.File
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
            model.chat("我的桌面上有哪些文件夹，大小分别是多少？")
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
                systemInstruction = functionCallPrompt(fileFunction){
                    appendLine("所有文件查询都可以且必须通过函数调用完成。单个函数调用不能解决问题就一次调用多个函数。")
                },
                messageModifier = { "$it\n如果你接下来需要调用函数，请先说明你调用的目的再进行调用；如果已经不需要再调用函数了，请根据所有信息给出最终的完整回复" }
            )
            respondent.chat(prompt[0])
        }
    }.exampleExplained("找出E:\\ACodeSpace目录下的所有文件夹，查询每一个的大小，并筛选出其中大小大于1MB的文件夹") { "找到2个文件夹：\nIDEA\nVSCode" },
)

val fileFunction = listOf(
    GptFunction {
        name("查询文件信息")
        addParam("路径", "文件或文件夹的完整路径")
        description("返回文件或文件夹的信息，包括名称、大小、类型，如果是文件夹，会统计文件夹下所有文件的大小总和")
        resolveWith { path ->
            val file = File(path[0])
            when {
                !file.exists() -> "文件不存在"
                file.isDirectory ->
                    "文件名：${file.name}\n文件夹大小：${file.walk().sumOf { it.length() }}字节\n类型：文件夹"

                else -> "文件名：${file.name}\n文件大小：${file.length()}字节\n类型：文件"
            }
        }
    }.exampleExplained("E:\\ACodeSpace\\IDEA\\langchain4kt\\build.gradle.kts"),
    GptFunction {
        name("列出路径下的文件与文件夹")
        addParam("路径", "绝对路径")
        description("返回路径下的所有文件与文件夹的名称与类型")
        resolveWith { path ->
            val file = File(path[0])
            if (!file.exists()) {
                "路径不存在"
            } else {
                val listFiles = file.listFiles()
                when {
                    listFiles == null -> "不是文件夹"
                    listFiles.isEmpty() -> "文件夹为空"
                    else -> listFiles.joinToString("\n") {
                        if (it.isDirectory) "文件夹：${it.name}" else "文件：${it.name}"
                    }
                }
            }
        }
    }.exampleExplained("E:\\ACodeSpace\\IDEA\\langchain4kt"),
)