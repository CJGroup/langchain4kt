import dev.langchain4j.model.dashscope.QwenChatModel
import io.github.stream29.langchain4kt.api.langchain4j.Langchain4jChatApiProvider

val apiProvider = Langchain4jChatApiProvider(
    QwenChatModel.builder()
        .apiKey(System.getenv("ALIBABA_QWEN_API_KEY")!!)
        .modelName("qwen-turbo-latest")
        .build()
)