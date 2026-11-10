package ru.rznnike.demokmp.app.ui.view

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme

/**
 * The force-close button is composed from the start but guarded by `isForceCloseButtonVisible`,
 * which flips only after the 5 s delay in `LaunchedEffect`. Alpha alone is not observable through
 * semantics, so the guard is asserted through its effect: a click must dispatch only after the delay.
 */
class AppClosingPlaceholderTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `does not dispatch force close before the delay elapses`() {
        // Arrange
        var forceCloseCount = 0
        rule.mainClock.autoAdvance = false
        rule.setContent {
            PreviewDesktopAppTheme {
                AppClosingPlaceholder(forceCloseCallback = { forceCloseCount++ })
            }
        }

        // Act
        rule.onNode(hasClickAction()).performClick()

        // Assert
        assertEquals(0, forceCloseCount)
    }

    @Test
    fun `dispatches force close after the delay elapses`() {
        // Arrange
        var forceCloseCount = 0
        rule.mainClock.autoAdvance = false
        rule.setContent {
            PreviewDesktopAppTheme {
                AppClosingPlaceholder(forceCloseCallback = { forceCloseCount++ })
            }
        }

        // Act
        rule.mainClock.advanceTimeBy(FORCE_CLOSE_BUTTON_DELAY_MS)
        rule.onNode(hasClickAction()).performClick()

        // Assert
        assertEquals(1, forceCloseCount)
    }

    private companion object {
        // Mirrors the private FORCE_CLOSE_BUTTON_DELAY_MS in AppClosingPlaceholder.kt.
        const val FORCE_CLOSE_BUTTON_DELAY_MS = 5000L
    }
}
