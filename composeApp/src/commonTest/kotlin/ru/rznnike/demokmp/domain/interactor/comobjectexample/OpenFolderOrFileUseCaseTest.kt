package ru.rznnike.demokmp.domain.interactor.comobjectexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeComObjectExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OpenFolderOrFileUseCaseTest {
    @Test
    fun `forwards the requested path to the gateway`() = runTest {
        // Arrange
        val gateway = FakeComObjectExampleGateway()
        val useCase = OpenFolderOrFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase("/some/path")

        // Assert
        assertEquals(listOf("openFolderOrFile:/some/path"), gateway.calls)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeComObjectExampleGateway().apply { errorToThrow = expected }
        val useCase = OpenFolderOrFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase("/some/path")

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
