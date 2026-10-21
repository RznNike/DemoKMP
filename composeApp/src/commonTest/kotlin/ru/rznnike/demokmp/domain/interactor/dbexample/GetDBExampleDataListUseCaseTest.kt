package ru.rznnike.demokmp.domain.interactor.dbexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeDBExampleGateway
import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetDBExampleDataListUseCaseTest {
    @Test
    fun `returns the list of rows received from the gateway`() = runTest {
        // Arrange
        val rows = listOf(
            DBExampleData(id = 1, name = "first"),
            DBExampleData(id = 2, name = "second"),
        )
        val gateway = FakeDBExampleGateway().apply { allResult = rows }
        val useCase = GetDBExampleDataListUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(rows, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns an empty list when the gateway has no rows`() = runTest {
        // Arrange
        val gateway = FakeDBExampleGateway()
        val useCase = GetDBExampleDataListUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(emptyList<DBExampleData>(), result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of the list`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeDBExampleGateway().apply { errorToThrow = expected }
        val useCase = GetDBExampleDataListUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
