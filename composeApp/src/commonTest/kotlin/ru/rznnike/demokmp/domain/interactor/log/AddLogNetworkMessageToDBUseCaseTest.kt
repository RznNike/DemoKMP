package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AddLogNetworkMessageToDBUseCaseTest {
    @Test
    fun `forwards the network message to the gateway and returns unit`() = runTest {
        // Arrange
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway()
        val useCase = AddLogNetworkMessageToDBUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(listOf(message), gateway.addedNetworkMessages)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = AddLogNetworkMessageToDBUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
