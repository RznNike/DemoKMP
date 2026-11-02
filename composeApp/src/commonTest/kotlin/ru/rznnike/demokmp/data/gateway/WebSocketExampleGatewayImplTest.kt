package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FakeAppWebSocketManager(
    private val url: String = "wss://example.com/websocket",
    connectionState: WebSocketConnectionState = WebSocketConnectionState.CONNECTED
) : AppWebSocketManager {
    // replay = 1 so a message emitted before collection is not lost
    private val messages = MutableSharedFlow<WebSocketMessageModel>(replay = 1)
    val sessionConnectionState = MutableStateFlow(connectionState)
    val sentMessages = mutableListOf<WebSocketMessageModel>()
    var closeSessionCallCount = 0

    fun emit(message: WebSocketMessageModel) {
        messages.tryEmit(message)
    }

    override fun getSession() = WebSocketSessionData(
        url = url,
        messages = messages,
        connectionState = sessionConnectionState
    )

    override suspend fun closeSession() {
        closeSessionCallCount++
    }

    override suspend fun sendMessage(message: WebSocketMessageModel) {
        sentMessages += message
    }
}

class WebSocketExampleGatewayImplTest {
    @Test
    fun `getSession maps url and connection state`() = runTest {
        // Arrange
        val fake = FakeAppWebSocketManager(
            url = "wss://example.com/ws",
            connectionState = WebSocketConnectionState.CONNECTING
        )
        val gateway: WebSocketExampleGateway = WebSocketExampleGatewayImpl(fake)

        // Act
        val result = gateway.getSession()

        // Assert
        assertEquals("wss://example.com/ws", result.url)
        assertEquals(WebSocketConnectionState.CONNECTING, result.connectionState.value)
    }

    @Test
    fun `getSession exposes live connection state updates`() = runTest {
        // Arrange
        val fake = FakeAppWebSocketManager(connectionState = WebSocketConnectionState.CONNECTING)
        val gateway: WebSocketExampleGateway = WebSocketExampleGatewayImpl(fake)
        val result = gateway.getSession()

        // Act
        fake.sessionConnectionState.value = WebSocketConnectionState.CONNECTED

        // Assert
        assertSame(fake.sessionConnectionState, result.connectionState)
        assertEquals(WebSocketConnectionState.CONNECTED, result.connectionState.value)
    }

    @Test
    fun `getSession maps network messages to incoming domain messages`() = runTest {
        // Arrange
        val fake = FakeAppWebSocketManager()
        val gateway: WebSocketExampleGateway = WebSocketExampleGatewayImpl(fake)
        val session = gateway.getSession()

        // Act
        fake.emit(WebSocketMessageModel(text = "hello"))

        // Assert
        assertEquals(WebSocketMessage(text = "hello", isIncoming = true), session.messages.first())
    }

    @Test
    fun `sendMessage converts message to network model and delegates`() = runTest {
        // Arrange
        val fake = FakeAppWebSocketManager()
        val gateway: WebSocketExampleGateway = WebSocketExampleGatewayImpl(fake)

        // Act
        gateway.sendMessage(WebSocketMessage(text = "ping", isIncoming = false))

        // Assert
        assertEquals(listOf(WebSocketMessageModel(text = "ping")), fake.sentMessages)
    }

    @Test
    fun `closeSession delegates to manager`() = runTest {
        // Arrange
        val fake = FakeAppWebSocketManager()
        val gateway: WebSocketExampleGateway = WebSocketExampleGatewayImpl(fake)

        // Act
        gateway.closeSession()

        // Assert
        assertEquals(1, fake.closeSessionCallCount)
    }
}
