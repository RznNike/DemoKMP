package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Theme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetThemeUseCaseTest {
    @Test
    fun `returns the theme stored in preferences`() = runTest {
        // Arrange
        val gateway = FakePreferencesGateway().apply { theme = Theme.DARK_CONTRAST }
        val useCase = GetThemeUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(Theme.DARK_CONTRAST, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = GetThemeUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
