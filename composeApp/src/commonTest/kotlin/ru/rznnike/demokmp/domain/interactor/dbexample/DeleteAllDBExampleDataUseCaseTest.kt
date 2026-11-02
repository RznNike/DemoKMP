package ru.rznnike.demokmp.domain.interactor.dbexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeDBExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeleteAllDBExampleDataUseCaseTest {
    @Test
    fun `deletes all the rows through the gateway`() = runTest {
        // Arrange
        val gateway = FakeDBExampleGateway()
        val useCase = DeleteAllDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(1, gateway.deleteAllCalls)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of deleting the rows`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeDBExampleGateway().apply { errorToThrow = expected }
        val useCase = DeleteAllDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertEquals(0, gateway.deleteAllCalls)
    }
}
