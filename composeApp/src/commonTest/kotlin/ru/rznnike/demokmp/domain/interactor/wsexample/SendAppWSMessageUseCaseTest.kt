package ru.rznnike.demokmp.domain.interactor.wsexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeWebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class SendAppWSMessageUseCaseTest {
    @Test
    fun `forwards the message to the gateway`() = runTest {
        // Arrange
        val message = WebSocketMessage(text = "hello", isIncoming = false)
        val gateway = FakeWebSocketExampleGateway()
        val useCase = SendAppWSMessageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(1, gateway.sentMessages.size)
        assertSame(message, gateway.sentMessages.first())
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of sending the message`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val message = WebSocketMessage(text = "hello", isIncoming = false)
        val gateway = FakeWebSocketExampleGateway().apply { errorToThrow = expected }
        val useCase = SendAppWSMessageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertEquals(0, gateway.sentMessages.size)
    }
}
