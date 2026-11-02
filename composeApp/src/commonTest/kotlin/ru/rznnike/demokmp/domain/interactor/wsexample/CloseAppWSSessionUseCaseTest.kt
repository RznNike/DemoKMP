package ru.rznnike.demokmp.domain.interactor.wsexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeWebSocketExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CloseAppWSSessionUseCaseTest {
    @Test
    fun `closes the web socket session through the gateway`() = runTest {
        // Arrange
        val gateway = FakeWebSocketExampleGateway()
        val useCase = CloseAppWSSessionUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(1, gateway.closeSessionCalls)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of closing the session`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeWebSocketExampleGateway().apply { errorToThrow = expected }
        val useCase = CloseAppWSSessionUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertEquals(0, gateway.closeSessionCalls)
    }
}
