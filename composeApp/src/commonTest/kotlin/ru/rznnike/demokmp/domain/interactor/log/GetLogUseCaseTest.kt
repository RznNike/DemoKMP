package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetLogUseCaseTest {
    @Test
    fun `returns the log data provided by the gateway`() = runTest {
        // Arrange
        val message = LogMessage(
            type = LogType.NETWORK,
            level = LogLevel.INFO,
            timestamp = 0L,
            tag = "Tag",
            message = "text",
        )
        val expected = LogData(log = listOf(message), eventsFlow = emptyFlow())
        val gateway = FakeLogGateway().apply { logData = expected }
        val useCase = GetLogUseCase(gateway, FakeDispatcherProvider())

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
        val useCase = GetLogUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
