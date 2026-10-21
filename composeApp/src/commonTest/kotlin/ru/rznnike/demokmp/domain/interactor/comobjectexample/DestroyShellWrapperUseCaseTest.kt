package ru.rznnike.demokmp.domain.interactor.comobjectexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeComObjectExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DestroyShellWrapperUseCaseTest {
    @Test
    fun `forwards the destroy request to the gateway`() = runTest {
        // Arrange
        val gateway = FakeComObjectExampleGateway()
        val useCase = DestroyShellWrapperUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(listOf("destroyShellWrapper"), gateway.calls)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeComObjectExampleGateway().apply { errorToThrow = expected }
        val useCase = DestroyShellWrapperUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
