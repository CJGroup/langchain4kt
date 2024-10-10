package chat.input

interface IChatConfig: MutableMap<String, Any>

class ChatConfig(
    private val map: MutableMap<String, Any> = mutableMapOf()
) : IChatConfig, MutableMap<String, Any> by map {
    companion object {
        fun of(vararg pairs: Pair<String, Any>): ChatConfig = ChatConfig(mutableMapOf(*pairs))
    }
    // TODO: add schema validation
}

inline fun <reified T : Any> IChatConfig.getSafeAs(key: String): T? = get(key) as? T