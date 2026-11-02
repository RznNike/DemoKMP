package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeleteOldLogsUseCaseTest {
    @Test
    fun `forwards the retention mode to the gateway and returns unit`() = runTest {
        // Arrange
        val retentionMode = LogsRetentionMode.LastNSessions(sessionsCount = 2)
        val gateway = FakeLogGateway()
        val useCase = DeleteOldLogsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(retentionMode)

        // Assert
        assertEquals(listOf<LogsRetentionMode>(retentionMode), gateway.deletedLogsRetentionModes)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = DeleteOldLogsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(LogsRetentionMode.All)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
