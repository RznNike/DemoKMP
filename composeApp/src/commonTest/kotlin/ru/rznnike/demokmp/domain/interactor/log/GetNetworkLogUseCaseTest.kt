package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetNetworkLogUseCaseTest {
    @Test
    fun `returns the network log data provided by the gateway`() = runTest {
        // Arrange
        val message = testNetworkLogMessage()
        val expected = NetworkLogData(log = listOf(message), eventsFlow = emptyFlow())
        val gateway = FakeLogGateway().apply { networkLogData = expected }
        val useCase = GetNetworkLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = GetNetworkLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
