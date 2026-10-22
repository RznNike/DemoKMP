package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SaveTextToFileUseCaseTest {
    @Test
    fun `forwards the file and text to the gateway`() = runTest {
        // Arrange
        val file = File("network.log")
        val text = "request body\nresponse body"
        val gateway = FakeLogGateway()
        val useCase = SaveTextToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(
            SaveTextToFileUseCase.Parameters(file = file, text = text)
        )

        // Assert
        assertEquals(listOf(file to text), gateway.savedTexts)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = SaveTextToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(
            SaveTextToFileUseCase.Parameters(file = File("network.log"), text = "text")
        )

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
