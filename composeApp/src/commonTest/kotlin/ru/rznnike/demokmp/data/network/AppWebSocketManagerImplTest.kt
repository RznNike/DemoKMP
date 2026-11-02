package ru.rznnike.demokmp.data.network

import com.russhwolf.settings.MapSettings
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import java.io.IOException
import kotlin.test.*

class FakeWebSocketSession : WebSocketSession {
    override val coroutineContext = SupervisorJob()

    override val incoming: ReceiveChannel<Frame>
        field = Channel<Frame>(Channel.UNLIMITED)
    override val outgoing: SendChannel<Frame>
        field = Channel<Frame>(Channel.UNLIMITED)
    override val extensions: List<WebSocketExtension<*>> = emptyList()
    override var maxFrameSize: Long = Long.MAX_VALUE
    override var masking: Boolean = false

    val sentFrames = mutableListOf<Frame>()

    fun push(frame: Frame) {
        incoming.trySend(frame)
    }

    override suspend fun send(frame: Frame) {
        sentFrames += frame
    }

    override suspend fun flush() = Unit

    @Suppress("OVERRIDE_DEPRECATION")
    override fun terminate() = cancel()
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppWebSocketManagerImplTest {
    private fun createManager(
        session: FakeWebSocketSession = FakeWebSocketSession(),
        sessionFactory: (suspend (String) -> WebSocketSession)? = null
    ) = AppWebSocketManagerImpl(
        client = HttpClient(OkHttp),
        preferencesManager = PreferencesManager(MapSettings()),
        webSocketSessionFactory = sessionFactory ?: { session }
    )

    private fun TestScope.collectMessages(
        manager: AppWebSocketManagerImpl,
        collected: MutableList<WebSocketMessageModel>
    ) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.getSession().messages.toList(collected)
        }
    }

    @Test
    fun `getSession uses the websocket url from build config`() {
        // Arrange
        val manager = createManager()

        // Act
        val result = manager.getSession()

        // Assert
        assertEquals(BuildKonfig.API_WEBSOCKETS, result.url)
    }

    @Test
    fun `connection state reaches connected when messages are collected`() = runTest {
        // Arrange
        val manager = createManager()
        val collected = mutableListOf<WebSocketMessageModel>()

        // Act
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Assert
        assertEquals(WebSocketConnectionState.CONNECTED, manager.getSession().connectionState.value)
    }

    @Test
    fun `valid json text frames are emitted as messages`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Act
        session.push(Frame.Text("""{"text":"hello"}"""))
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(WebSocketMessageModel(text = "hello")), collected)
    }

    @Test
    fun `frames with invalid json are skipped`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Act
        session.push(Frame.Text("not a json"))
        advanceUntilIdle()

        // Assert
        assertTrue(collected.isEmpty())
    }

    @Test
    fun `binary, ping and pong frames are ignored`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Act
        session.push(Frame.Binary(fin = true, data = byteArrayOf(1, 2, 3)))
        session.push(Frame.Ping(byteArrayOf(1)))
        session.push(Frame.Pong(byteArrayOf(1)))
        advanceUntilIdle()

        // Assert
        assertTrue(collected.isEmpty())
    }

    @Test
    fun `close frame closes the session and sets state to closed`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()
        val sessionData = manager.getSession()

        // Act
        session.push(Frame.Close(CloseReason(CloseReason.Codes.NORMAL, "bye")))
        advanceUntilIdle()

        // Assert
        assertEquals(WebSocketConnectionState.CLOSED, sessionData.connectionState.value)
    }

    @Test
    fun `connection error retries and reconnects`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        var attempts = 0
        val manager = createManager(session) {
            attempts++
            if (attempts == 1) throw IOException("network down")
            session
        }
        val collected = mutableListOf<WebSocketMessageModel>()

        // Act
        // Test-scope collector: advanceUntilIdle drives virtual time for foreground work only,
        // so the retry delay would never elapse for a backgroundScope collector.
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.getSession().messages.toList(collected)
        }
        advanceUntilIdle()

        // Assert
        assertEquals(2, attempts)
        assertEquals(WebSocketConnectionState.CONNECTED, manager.getSession().connectionState.value)

        // The reconnected session is live and receives messages
        session.push(Frame.Text("""{"text":"after retry"}"""))
        advanceUntilIdle()
        assertEquals(listOf(WebSocketMessageModel(text = "after retry")), collected)

        collector.cancelAndJoin()
    }

    @Test
    fun `sendMessage sends json encoded frame to the session`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Act
        manager.sendMessage(WebSocketMessageModel(text = "ping"))

        // Assert
        val sentTexts = session.sentFrames.map { (it as? Frame.Text)?.readText() }
        assertEquals(listOf("""{"text":"ping"}"""), sentTexts)
    }

    @Test
    fun `sendMessage without session does nothing`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)

        // Act
        manager.sendMessage(WebSocketMessageModel(text = "ping"))

        // Assert
        assertTrue(session.sentFrames.isEmpty())
    }

    @Test
    fun `closeSession without session does not fail`() = runTest {
        // Arrange
        val manager = createManager()

        // Act & Assert
        manager.closeSession()
    }

    @Test
    fun `getSession caches session data until closed`() = runTest {
        // Arrange
        val manager = createManager()

        // Act
        val first = manager.getSession()
        val second = manager.getSession()
        manager.closeSession()
        val third = manager.getSession()

        // Assert
        assertSame(first, second)
        assertNotSame(first, third)
    }

    @Test
    fun `closeSession on an open session sends close frame and resets state`() = runTest {
        // Arrange
        val session = FakeWebSocketSession()
        val manager = createManager(session)
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()
        val sessionData = manager.getSession()
        assertEquals(WebSocketConnectionState.CONNECTED, sessionData.connectionState.value)

        // Act
        manager.closeSession()

        // Assert
        val sentFrames = session.sentFrames
        assertTrue(sentFrames.any { it is Frame.Close })
        assertEquals(WebSocketConnectionState.CLOSED, sessionData.connectionState.value)
        // After close, session data should be cleared so getSession creates a new one
        val newSessionData = manager.getSession()
        assertNotSame(sessionData, newSessionData)
    }

    @Test
    fun `sendMessage propagates a send failure to the caller`() = runTest {
        // Arrange
        val failingSession = object : WebSocketSession {
            override val coroutineContext = SupervisorJob()
            override val incoming: ReceiveChannel<Frame> = Channel(Channel.UNLIMITED)
            override val outgoing: SendChannel<Frame> = Channel(Channel.UNLIMITED)
            override val extensions: List<WebSocketExtension<*>> = emptyList()
            override var maxFrameSize: Long = Long.MAX_VALUE
            override var masking: Boolean = false

            override suspend fun send(frame: Frame) {
                throw IOException("send failed")
            }

            override suspend fun flush() = Unit

            @Suppress("OVERRIDE_DEPRECATION")
            override fun terminate() = cancel()
        }
        val manager = createManager(sessionFactory = { failingSession })
        val collected = mutableListOf<WebSocketMessageModel>()
        collectMessages(manager, collected)
        advanceUntilIdle()

        // Act & Assert
        assertFailsWith<IOException> {
            manager.sendMessage(WebSocketMessageModel(text = "ping"))
        }
    }

    @Test
    fun `retry predicate only retries on IOException and resets state on non io errors`() = runTest {
        // Arrange - non-IO error on connection
        val session = FakeWebSocketSession()
        var attempts = 0
        val manager = createManager(session) {
            attempts++
            throw IllegalStateException("bad state")
        }
        val collected = mutableListOf<WebSocketMessageModel>()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            try {
                manager.getSession().messages.toList(collected)
            } catch (_: Exception) {
            }
        }
        advanceUntilIdle()

        // Assert - should not retry on non-IOException
        assertEquals(1, attempts)
        assertNotEquals(WebSocketConnectionState.CONNECTED, manager.getSession().connectionState.value)
        collector.cancel()
    }
}
