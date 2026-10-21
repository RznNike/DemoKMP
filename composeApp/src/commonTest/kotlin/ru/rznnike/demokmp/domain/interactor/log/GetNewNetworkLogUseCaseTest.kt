package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetNewNetworkLogUseCaseTest {
    @Test
    fun `forwards the last id to the gateway and returns the new network messages`() = runTest {
        // Arrange
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway().apply { newNetworkLogMessages = listOf(message) }
        val useCase = GetNewNetworkLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42L)

        // Assert
        assertEquals(listOf(42L), gateway.requestedLastNetworkLogIds)
        assertEquals(listOf(message), result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = GetNewNetworkLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42L)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
