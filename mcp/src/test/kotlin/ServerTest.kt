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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.concurrent.Volatile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

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
    private var string: String? = null

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
        server.addTool("putString", "show text") { it: String -> string = it }
    }

    @Test
    fun testServer() = runBlocking {
        assertTrue { client.listTools()!!.tools.asSequence().map { it.name }.contains("putString") }
        client.callTool("putString", mapOf("value" to "Hello world!"))
        assertEquals("Hello world!", string)
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