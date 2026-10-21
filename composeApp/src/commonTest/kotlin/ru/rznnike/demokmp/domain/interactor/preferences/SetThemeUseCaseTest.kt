package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Theme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetThemeUseCaseTest {
    @Test
    fun `forwards the theme to the preferences gateway`() = runTest {
        // Arrange
        val gateway = FakePreferencesGateway()
        val useCase = SetThemeUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(Theme.DARK)

        // Assert
        assertEquals(listOf(Theme.DARK), gateway.setThemeValues)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of forwarding the theme`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = SetThemeUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(Theme.DARK)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.setThemeValues.isEmpty())
    }
}
