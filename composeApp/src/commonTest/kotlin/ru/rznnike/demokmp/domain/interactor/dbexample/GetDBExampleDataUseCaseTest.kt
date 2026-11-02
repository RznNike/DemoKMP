package ru.rznnike.demokmp.domain.interactor.dbexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeDBExampleGateway
import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetDBExampleDataUseCaseTest {
    @Test
    fun `requests data by id and wraps it in a result`() = runTest {
        // Arrange
        val gateway = FakeDBExampleGateway().apply { getResult = DBExampleData(id = 7, name = "row") }
        val useCase = GetDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(GetDBExampleDataUseCase.Parameters(id = 7))

        // Assert
        assertEquals(listOf(7L), gateway.requestedIds)
        assertEquals(DBExampleData(id = 7, name = "row"), result.data?.data)
        assertNull(result.error)
    }

    @Test
    fun `keeps a missing row as null data`() = runTest {
        // Arrange
        val useCase = GetDBExampleDataUseCase(FakeDBExampleGateway(), FakeDispatcherProvider())

        // Act
        val result = useCase(GetDBExampleDataUseCase.Parameters(id = 1))

        // Assert
        // The wrapper is present, only the payload is null - so the caller is notified about success.
        val payload = result.data
        assertNotNull(payload)
        assertNull(payload.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeDBExampleGateway().apply { errorToThrow = expected }
        val useCase = GetDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(GetDBExampleDataUseCase.Parameters(id = 7))

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
