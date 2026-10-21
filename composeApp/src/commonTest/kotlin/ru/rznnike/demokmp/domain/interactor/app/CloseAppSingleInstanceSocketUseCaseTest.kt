package ru.rznnike.demokmp.domain.interactor.app

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeAppGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CloseAppSingleInstanceSocketUseCaseTest {
    @Test
    fun `closes the single instance socket through the gateway`() = runTest {
        // Arrange
        val gateway = FakeAppGateway()
        val useCase = CloseAppSingleInstanceSocketUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(1, gateway.closeSingleInstanceSocketCalls)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of closing the socket`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeAppGateway().apply { errorToThrow = expected }
        val useCase = CloseAppSingleInstanceSocketUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertEquals(0, gateway.closeSingleInstanceSocketCalls)
    }
}
