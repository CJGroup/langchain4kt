package chat.input

interface NameMappingConfig: MutableMap<String, Any>

class ChatConfig(
    private val map: MutableMap<String, Any> = mutableMapOf()
) : NameMappingConfig, MutableMap<String, Any> by map {
    companion object {
        fun of(vararg pairs: Pair<String, Any>): ChatConfig = ChatConfig(mutableMapOf(*pairs))
    }
    // TODO: add schema validation
}

inline fun <reified T : Any> NameMappingConfig.getSafeAs(key: String): T? = get(key) as? T

inline fun <reified T : Any> NameMappingConfig.getOrThrow(key: String): T =
    getSafeAs(key) ?: throw NoSuchElementException("Config key $key not found")