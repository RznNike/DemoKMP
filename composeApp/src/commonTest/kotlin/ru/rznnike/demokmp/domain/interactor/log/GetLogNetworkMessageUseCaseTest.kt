package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetLogNetworkMessageUseCaseTest {
    @Test
    fun `requests the message by uuid and wraps it in a result`() = runTest {
        // Arrange
        val uuid = UUID.randomUUID()
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway().apply { networkMessage = message }
        val useCase = GetLogNetworkMessageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(uuid)

        // Assert
        assertEquals(listOf(uuid), gateway.requestedMessageUuids)
        assertEquals(message, result.data?.message)
        assertNull(result.error)
    }

    @Test
    fun `keeps a missing message as null`() = runTest {
        // Arrange
        val useCase = GetLogNetworkMessageUseCase(FakeLogGateway(), FakeDispatcherProvider())

        // Act
        val result = useCase(UUID.randomUUID())

        // Assert
        assertNull(result.data?.message)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = GetLogNetworkMessageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(UUID.randomUUID())

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
