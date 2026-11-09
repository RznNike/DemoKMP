package ru.rznnike.demokmp.app.ui.item

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.utils.currentTimeMillis

/**
 * Smoke coverage for the logger list row: tag rendering, `getFormattedMessage()` normalization and
 * the `collapseNetworkMessages` branch.
 *
 * Highlight *styling* produced by `highlightSubstrings(query)` is not exposed through semantics
 * (only the resulting text is), so it cannot be asserted here — an accepted gap.
 */
class LogMessageItemTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `renders the message tag`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                LogMessageItem(
                    message = logMessage(tag = "DemoTag"),
                    query = "",
                    filterOnlyByTag = false,
                    collapseNetworkMessages = false
                )
            }
        }

        // Assert
        rule.onNodeWithText("DemoTag").assertExists()
    }

    @Test
    fun `renders tabs as four spaces`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                LogMessageItem(
                    message = logMessage(message = "First\tpart"),
                    query = "",
                    filterOnlyByTag = false,
                    collapseNetworkMessages = false
                )
            }
        }

        // Assert
        rule.onNodeWithText("First    part").assertExists()
    }

    @Test
    fun `collapses a network message to its first line`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                LogMessageItem(
                    message = logMessage(type = LogType.NETWORK, message = MULTILINE_MESSAGE),
                    query = "",
                    filterOnlyByTag = false,
                    collapseNetworkMessages = true
                )
            }
        }

        // Assert
        rule.onNodeWithText(SECOND_LINE, substring = true).assertDoesNotExist()
    }

    @Test
    fun `keeps every line when collapsing is disabled`() {
        // Arrange
        rule.setContent {
            PreviewDesktopAppTheme {
                LogMessageItem(
                    message = logMessage(type = LogType.NETWORK, message = MULTILINE_MESSAGE),
                    query = "",
                    filterOnlyByTag = false,
                    collapseNetworkMessages = false
                )
            }
        }

        // Assert
        rule.onNodeWithText(SECOND_LINE, substring = true).assertExists()
    }

    private fun logMessage(
        type: LogType = LogType.DEFAULT,
        tag: String = "",
        message: String = "Demo message"
    ) = LogMessage(
        type = type,
        level = LogLevel.DEBUG,
        timestamp = currentTimeMillis(),
        tag = tag,
        message = message
    )

    private companion object {
        const val SECOND_LINE = "Second line"
        const val MULTILINE_MESSAGE = "First line\n$SECOND_LINE"
    }
}
