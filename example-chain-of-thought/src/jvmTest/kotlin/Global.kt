import dev.langchain4j.model.dashscope.QwenChatModel
import io.github.stream29.langchain4kt.api.langchain4kt.Langchain4jApiProvider

val apiProvider = Langchain4jApiProvider(
    QwenChatModel.builder()
        .apiKey(System.getenv("ALIBABA_QWEN_API_KEY")!!)
        .modelName("qwen-turbo-latest")
        .build()
)