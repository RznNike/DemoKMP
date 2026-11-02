package ru.rznnike.demokmp.domain.interactor.wsexample

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeWebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class GetAppWSSessionUseCaseTest {
    @Test
    fun `returns the session provided by the gateway`() = runTest {
        // Arrange
        val session = WebSocketSessionData(
            url = "wss://example.com",
            messages = emptyFlow<WebSocketMessage>(),
            connectionState = MutableStateFlow(WebSocketConnectionState.CONNECTED)
        )
        val gateway = FakeWebSocketExampleGateway().apply { this.session = session }
        val useCase = GetAppWSSessionUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertSame(session, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of a session`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeWebSocketExampleGateway().apply { errorToThrow = expected }
        val useCase = GetAppWSSessionUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
