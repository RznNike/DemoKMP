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

class AddLogMessageToDBUseCaseTest {
    @Test
    fun `forwards the message to the gateway and returns unit`() = runTest {
        // Arrange
        val message = LogMessage(
            type = LogType.NETWORK,
            level = LogLevel.INFO,
            timestamp = 0L,
            tag = "Tag",
            message = "text",
        )
        val gateway = FakeLogGateway()
        val useCase = AddLogMessageToDBUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(listOf(message), gateway.addedMessages)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val message = LogMessage(
            type = LogType.NETWORK,
            level = LogLevel.INFO,
            timestamp = 0L,
            tag = "Tag",
            message = "text",
        )
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = AddLogMessageToDBUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(message)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
