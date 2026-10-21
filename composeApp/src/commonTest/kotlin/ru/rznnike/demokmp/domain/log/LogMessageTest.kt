package ru.rznnike.demokmp.domain.log

import kotlin.test.Test
import kotlin.test.assertEquals

private fun logMessage(text: String) = LogMessage(
    type = LogType.DEFAULT,
    level = LogLevel.INFO,
    timestamp = 0L,
    tag = "Tag",
    message = text
)

class LogMessageTest {
    @Test
    fun `getFormattedMessage trims outer whitespace and replaces tabs`() {
        // Act
        val result = logMessage("  line1\n\tline2\t  ").getFormattedMessage()

        // Assert
        assertEquals("line1\n    line2", result)
    }

    @Test
    fun `getFormattedMessage returns an empty string for a blank message`() {
        // Act
        val result = logMessage("  \t\n ").getFormattedMessage()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `getFormattedMessage preserves inner spacing`() {
        // Act
        val result = logMessage("a  b").getFormattedMessage()

        // Assert
        assertEquals("a  b", result)
    }
}
