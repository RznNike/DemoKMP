package ru.rznnike.demokmp.domain.interactor.comobjectexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeComObjectExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetPCDataUseCaseTest {
    @Test
    fun `returns the pc data received from the gateway`() = runTest {
        // Arrange
        val gateway = FakeComObjectExampleGateway().apply { pcData = "some data" }
        val useCase = GetPCDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(listOf("getPCData"), gateway.calls)
        assertEquals("some data", result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeComObjectExampleGateway().apply { errorToThrow = expected }
        val useCase = GetPCDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
