package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetLogNetworkMessageAsFlowUseCaseTest {
    @Test
    fun `forwards uuid and emits the gateway values`() = runTest {
        // Arrange
        val uuid = UUID.randomUUID()
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway().apply { networkMessageFlow = flowOf(message) }
        val useCase = GetLogNetworkMessageAsFlowUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(uuid).toList()

        // Assert
        assertEquals(listOf(uuid), gateway.flowRequestedUuids)
        assertEquals(listOf<NetworkLogMessage?>(message), result)
    }

    @Test
    fun `emits null when the gateway has no message`() = runTest {
        // Arrange
        val gateway = FakeLogGateway().apply { networkMessageFlow = flowOf(null) }
        val useCase = GetLogNetworkMessageAsFlowUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(UUID.randomUUID()).toList()

        // Assert
        assertEquals(listOf<NetworkLogMessage?>(null), result)
    }

    @Test
    fun `propagates the gateway error to the caller`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = GetLogNetworkMessageAsFlowUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = runCatching { useCase(UUID.randomUUID()) }

        // Assert
        // Flow use cases do not wrap failures into UseCaseResult - the exception reaches the caller as is.
        assertTrue(result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }
}
