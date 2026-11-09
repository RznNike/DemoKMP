package ru.rznnike.demokmp.app.ui.dialog.common

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme

/**
 * Smoke coverage for the shared alert dialog: header / message rendering and action dispatch.
 *
 * The dialog carries plain `String` parameters, so no Compose resources and no DI (Koin, ViewModel,
 * window) are involved — only `PreviewDesktopAppTheme` for Material colors and shapes.
 */
class CommonAlertDialogTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `renders the header and the message`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                CommonAlertDialog(
                    type = AlertDialogType.HORIZONTAL,
                    header = HEADER,
                    message = MESSAGE,
                    actions = listOf(AlertDialogAction(text = "OK") {})
                )
            }
        }

        // Assert
        rule.onNodeWithText(HEADER).assertExists()
        rule.onNodeWithText(MESSAGE).assertExists()
    }

    @Test
    fun `dispatches the tapped action callback`() {
        // Arrange
        var callbackCount = 0
        rule.setContent {
            PreviewDesktopAppTheme {
                CommonAlertDialog(
                    type = AlertDialogType.HORIZONTAL,
                    header = HEADER,
                    actions = listOf(
                        AlertDialogAction(text = "Cancel") {},
                        AlertDialogAction(text = "Confirm") { callbackCount++ }
                    )
                )
            }
        }

        // Act
        rule.onNodeWithText("Confirm").performClick()

        // Assert
        assertEquals(1, callbackCount)
    }

    @Test
    fun `renders every action in the vertical layout`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                CommonAlertDialog(
                    type = AlertDialogType.VERTICAL,
                    header = HEADER,
                    actions = listOf(
                        AlertDialogAction(text = "First") {},
                        AlertDialogAction(text = "Second") {}
                    )
                )
            }
        }

        // Assert
        rule.onNodeWithText("First").assertExists()
        rule.onNodeWithText("Second").assertExists()
    }

    private companion object {
        const val HEADER = "Dialog header"
        const val MESSAGE = "Dialog message"
    }
}
