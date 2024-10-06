package chat.input

interface IChatConfig {
    fun getAny(key: String): Any?
    fun setAny(key: String, value: Any)
    fun setStrict(key: String, value: Any)
}

class ChatConfig(
    sourceMap: Map<String, Any> = mutableMapOf()
) : IChatConfig {
    private val map = sourceMap.toMutableMap()

    override fun getAny(key: String): Any? = map[key]
    override fun setAny(key: String, value: Any) {
        map[key] = value
    }
    override fun setStrict(key: String, value: Any) {
        getAny(key)?.let {
            if(!it::class.isInstance(value))
                throw IllegalArgumentException("Type mismatch for key \"$key\", expected ${it::class.simpleName}, got ${value::class.simpleName}")
        }
        setAny(key, value)
    }
    // TODO: add schema validation
}

inline operator fun <reified T : Any> IChatConfig.get(key: String): T? = getAny(key) as? T
inline operator fun <reified T : Any> IChatConfig.set(key: String, value: T) = setStrict(key, value)