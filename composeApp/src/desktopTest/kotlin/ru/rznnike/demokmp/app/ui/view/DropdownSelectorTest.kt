package ru.rznnike.demokmp.app.ui.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme

/**
 * Smoke coverage for the dropdown used by the settings screen (theme / ui-scale selectors):
 * expand, list items, emit the tapped item and collapse again.
 *
 * Plain strings are used instead of the real `Theme` / `UiScale` enums — the contract under test is
 * the control's callback dispatch, not the domain enum values.
 */
class DropdownSelectorTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `expands and lists every item on tap`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                DropdownSelector(
                    label = LABEL,
                    items = ITEMS,
                    selectedItem = ITEMS.first(),
                    itemNameRetriever = { it ?: "" },
                    onItemSelected = {}
                )
            }
        }

        // Act
        rule.onNodeWithText(ITEMS.first()).performClick()

        // Assert
        rule.onNodeWithText("Beta").assertExists()
        rule.onNodeWithText("Gamma").assertExists()
    }

    @Test
    fun `emits the tapped item`() {
        // Arrange
        var selectedItem = ITEMS.first()
        rule.setContent {
            PreviewDesktopAppTheme {
                var currentItem by remember { mutableStateOf(ITEMS.first()) }
                DropdownSelector(
                    label = LABEL,
                    items = ITEMS,
                    selectedItem = currentItem,
                    itemNameRetriever = { it ?: "" },
                    onItemSelected = {
                        currentItem = it
                        selectedItem = it
                    }
                )
            }
        }

        // Act
        rule.onNodeWithText(ITEMS.first()).performClick()
        rule.onNodeWithText("Gamma").performClick()

        // Assert
        assertEquals("Gamma", selectedItem)
    }

    @Test
    fun `collapses the list after a selection`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                var currentItem by remember { mutableStateOf(ITEMS.first()) }
                DropdownSelector(
                    label = LABEL,
                    items = ITEMS,
                    selectedItem = currentItem,
                    itemNameRetriever = { it ?: "" },
                    onItemSelected = { currentItem = it }
                )
            }
        }

        // Act
        rule.onNodeWithText(ITEMS.first()).performClick()
        rule.onNodeWithText("Beta").performClick()

        // Assert
        rule.onNodeWithText("Gamma").assertDoesNotExist()
    }

    private companion object {
        const val LABEL = "Selector label"
        val ITEMS = listOf("Alpha", "Beta", "Gamma")
    }
}
