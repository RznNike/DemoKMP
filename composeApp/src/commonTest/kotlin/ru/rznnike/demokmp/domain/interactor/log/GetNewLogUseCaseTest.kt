package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetNewLogUseCaseTest {
    @Test
    fun `forwards the last id to the gateway and returns the new messages`() = runTest {
        // Arrange
        val message = LogMessage(
            type = LogType.NETWORK,
            level = LogLevel.INFO,
            timestamp = 0L,
            tag = "Tag",
            message = "text",
        )
        val gateway = FakeLogGateway().apply { newLogMessages = listOf(message) }
        val useCase = GetNewLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42L)

        // Assert
        assertEquals(listOf(42L), gateway.requestedLastLogIds)
        assertEquals(listOf(message), result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = GetNewLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42L)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
