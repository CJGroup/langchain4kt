@JvmInline
value class PluginCollector<PluginType>(
    val plugins: MutableList<PluginType> = mutableListOf()
) {
    fun PluginType.install() {
        plugins.add(this)
    }
}

class Plugin<T>

fun <T> logging() = Plugin<T>()

class ApiProvider

fun ApiProvider.plugins(block: PluginCollector<Plugin<ApiProvider>>.() -> Unit) {
    val collector = PluginCollector<Plugin<ApiProvider>>()
    collector.block()
}

fun main() {
    ApiProvider().plugins {
        logging().install()
    }
}