package ru.rznnike.demokmp.domain.interactor.dbexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeDBExampleGateway
import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AddDBExampleDataUseCaseTest {
    @Test
    fun `forwards the given data to the gateway`() = runTest {
        // Arrange
        val data = DBExampleData(id = 1, name = "row")
        val gateway = FakeDBExampleGateway()
        val useCase = AddDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(data)

        // Assert
        assertSame(data, gateway.addedData.single())
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of adding the data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeDBExampleGateway().apply { errorToThrow = expected }
        val useCase = AddDBExampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(DBExampleData(id = 1, name = "row"))

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.addedData.isEmpty())
    }
}
