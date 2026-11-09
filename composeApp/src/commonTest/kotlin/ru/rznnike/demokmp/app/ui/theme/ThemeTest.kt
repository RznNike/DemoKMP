package ru.rznnike.demokmp.app.ui.theme

import ru.rznnike.demokmp.domain.model.common.Theme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers the pure theme resolution extracted from `AppTheme`
 */
class ThemeTest {
    @Test
    fun `DARK is a dark theme`() {
        // Act
        val result = Theme.DARK.isDark()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `DARK_CONTRAST is a dark theme`() {
        // Act
        val result = Theme.DARK_CONTRAST.isDark()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `LIGHT is not a dark theme`() {
        // Act
        val result = Theme.LIGHT.isDark()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `LIGHT_CONTRAST is not a dark theme`() {
        // Act
        val result = Theme.LIGHT_CONTRAST.isDark()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `AUTO is not resolved as dark on its own`() {
        // Act
        val result = Theme.AUTO.isDark()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `AUTO resolves to DARK when the system theme is dark`() {
        // Act
        val result = resolveTheme(theme = Theme.AUTO, systemIsDark = true)

        // Assert
        assertEquals(Theme.DARK, result)
    }

    @Test
    fun `AUTO resolves to LIGHT when the system theme is light`() {
        // Act
        val result = resolveTheme(theme = Theme.AUTO, systemIsDark = false)

        // Assert
        assertEquals(Theme.LIGHT, result)
    }

    @Test
    fun `an explicit theme is passed through regardless of the system theme`() {
        // Arrange
        val explicitThemes = listOf(
            Theme.LIGHT,
            Theme.LIGHT_CONTRAST,
            Theme.DARK,
            Theme.DARK_CONTRAST
        )

        // Act & Assert
        for (theme in explicitThemes) {
            assertEquals(theme, resolveTheme(theme = theme, systemIsDark = true))
            assertEquals(theme, resolveTheme(theme = theme, systemIsDark = false))
        }
    }
}
