package ru.rznnike.demokmp.app.ui.view

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme
import ru.rznnike.demokmp.app.utils.getFormattedAppVersion
import ru.rznnike.demokmp.domain.model.common.UiScale

/**
 * Smoke coverage for the desktop status bar: content must follow `isVisible` and `uiScale`, and the
 * version label must come from [getFormattedAppVersion].
 *
 * `PreviewDesktopAppTheme` is used instead of `DesktopAppTheme`, because the latter resolves
 * `AppConfigurationViewModel` through `koinInject()` and would need a global Koin context.
 *
 * Assertions target non-resource text (`"%d%%"` and the version string): `stringResource` returns
 * an empty string on the first frame, so resource-backed labels would need polling.
 */
class BottomStatusBarTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `renders the formatted app version when visible`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                BottomStatusBar(
                    isVisible = true,
                    uiScale = UiScale.S100,
                    onLoggerClick = {},
                    onKeyboardShortcutsClick = {},
                    onUiScaleChanged = {}
                )
            }
        }

        // Assert
        rule.onNodeWithText(getFormattedAppVersion()).assertExists()
    }

    @Test
    fun `renders the current ui scale as a percentage when visible`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                BottomStatusBar(
                    isVisible = true,
                    uiScale = UiScale.S70,
                    onLoggerClick = {},
                    onKeyboardShortcutsClick = {},
                    onUiScaleChanged = {}
                )
            }
        }

        // Assert
        rule.onNodeWithText("70%").assertExists()
    }

    @Test
    fun `renders nothing when hidden`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                BottomStatusBar(
                    isVisible = false,
                    uiScale = UiScale.S100,
                    onLoggerClick = {},
                    onKeyboardShortcutsClick = {},
                    onUiScaleChanged = {}
                )
            }
        }

        // Assert
        rule.onNodeWithText(getFormattedAppVersion()).assertDoesNotExist()
    }
}
