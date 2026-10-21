package ru.rznnike.demokmp.domain.interactor.file

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeFileGateway
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CopyFileUseCaseTest {
    @Test
    fun `forwards original and copy files to the gateway`() = runTest {
        // Arrange
        val gateway = FakeFileGateway()
        val original = File("original.txt")
        val copy = File("copy.txt")
        val useCase = CopyFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(CopyFileUseCase.Parameters(original = original, copy = copy))

        // Assert
        assertEquals(listOf(original to copy), gateway.copiedFiles)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeFileGateway().apply { errorToThrow = expected }
        val useCase = CopyFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(CopyFileUseCase.Parameters(original = File("original.txt"), copy = File("copy.txt")))

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
