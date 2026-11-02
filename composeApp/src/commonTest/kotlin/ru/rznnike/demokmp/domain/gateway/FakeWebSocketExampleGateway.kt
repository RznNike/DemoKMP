package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

/**
 * Test double for [WebSocketExampleGateway].
 *
 * Every sent message is recorded in [sentMessages] so tests can assert the forwarded payload.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeWebSocketExampleGateway : WebSocketExampleGateway {
    var errorToThrow: Exception? = null

    var session: WebSocketSessionData<WebSocketMessage> = WebSocketSessionData(
        url = "wss://example.com",
        messages = emptyFlow(),
        connectionState = MutableStateFlow(WebSocketConnectionState.CONNECTED)
    )
    val sentMessages = mutableListOf<WebSocketMessage>()
    var closeSessionCalls = 0

    override suspend fun getSession(): WebSocketSessionData<WebSocketMessage> {
        errorToThrow?.let { throw it }
        return session
    }

    override suspend fun closeSession() {
        errorToThrow?.let { throw it }
        closeSessionCalls++
    }

    override suspend fun sendMessage(message: WebSocketMessage) {
        errorToThrow?.let { throw it }
        sentMessages += message
    }
}
