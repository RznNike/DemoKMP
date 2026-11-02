package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SaveLogToFileUseCaseTest {
    @Test
    fun `forwards the file to the gateway and returns unit`() = runTest {
        // Arrange
        val file = File("log.txt")
        val gateway = FakeLogGateway()
        val useCase = SaveLogToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(file)

        // Assert
        assertEquals(listOf(file), gateway.savedLogFiles)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = SaveLogToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(File("log.txt"))

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
