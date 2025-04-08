import io.github.stream29.langchain4kt2.mcp.addTool
import io.modelcontextprotocol.kotlin.sdk.ClientCapabilities
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.JSONRPCMessage
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.ClientOptions
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.shared.Transport
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ServerTest {
    private val server = Server(
        serverInfo = Implementation(
            name = "MyServer",
            version = "0.0.1",
        ),
        options = ServerOptions(
            capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true))
        )
    )

    private val client = Client(
        clientInfo = Implementation("MyClient", "0.0.1"),
        options = ClientOptions(
            capabilities = ClientCapabilities()
        )
    )

    private val fromClientToServer = Channel<JSONRPCMessage>()
    private val fromServerToClient = Channel<JSONRPCMessage>()

    @BeforeTest
    fun init() = runBlocking {
        server.connect(
            MockTransport(
                inputChannel = fromClientToServer,
                outputChannel = fromServerToClient
            )
        )
        client.connect(
            MockTransport(
                inputChannel = fromServerToClient,
                outputChannel = fromClientToServer
            )
        )
    }

    @Test
    fun testServer() = runBlocking {
        println("Test started")
        server.addTool("println", "print to stdout and \\n", ::println)
        val listTools = client.listTools()
        println(Json.encodeToString(listTools))
        client.callTool("println", mapOf("value" to "Hello world!"))
        Unit
    }

    fun println(message: String) {
        kotlin.io.println(message)
    }

    @AfterTest
    fun close() {
        runBlocking {
            server.close()
            client.close()
        }
    }
}

class MockTransport(
    private val inputChannel: ReceiveChannel<JSONRPCMessage>,
    private val outputChannel: SendChannel<JSONRPCMessage>,
) : Transport {

    @Volatile
    private var onClose: (() -> Unit)? = null

    @Volatile
    private var onError: ((Throwable) -> Unit)? = null

    @Volatile
    private var onMessage: (suspend (JSONRPCMessage) -> Unit)? = null

    @Volatile
    private var listeningJob: Job? = null

    override suspend fun close() {
        onClose?.invoke()
        listeningJob?.cancel()
    }

    override fun onClose(block: () -> Unit) {
        onClose = block
    }

    override fun onError(block: (Throwable) -> Unit) {
        onError = block
    }

    override fun onMessage(block: suspend (JSONRPCMessage) -> Unit) {
        onMessage = block
    }

    override suspend fun send(message: JSONRPCMessage) {
        outputChannel.send(message)
    }

    override suspend fun start() {
        listeningJob = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            for (msg in inputChannel) {
                try {
                    onMessage?.invoke(msg)
                } catch (e: Throwable) {
                    break
                }
            }
        }
    }

}