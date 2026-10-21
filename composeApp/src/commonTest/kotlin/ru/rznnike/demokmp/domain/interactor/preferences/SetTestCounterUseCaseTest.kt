package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetTestCounterUseCaseTest {
    @Test
    fun `forwards the test counter to the preferences gateway`() = runTest {
        // Arrange
        val gateway = FakePreferencesGateway()
        val useCase = SetTestCounterUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42)

        // Assert
        assertEquals(listOf(42), gateway.setTestCounterValues)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of forwarding the test counter`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = SetTestCounterUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(42)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.setTestCounterValues.isEmpty())
    }
}
