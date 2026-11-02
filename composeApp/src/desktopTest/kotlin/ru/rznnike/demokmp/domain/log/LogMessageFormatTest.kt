package ru.rznnike.demokmp.domain.log

import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TIMESTAMP = 1_700_000_000_000L

private fun Long.timeString(): String = toDateString(GlobalConstants.DATE_PATTERN_TIME_MS)

private fun logMessage(
    level: LogLevel = LogLevel.INFO,
    tag: String = "Tag",
    message: String = "message",
    timestamp: Long = TIMESTAMP
) = LogMessage(
    type = LogType.DEFAULT,
    level = level,
    timestamp = timestamp,
    tag = tag,
    message = message
)

class LogMessageFormatTest {
    @Test
    fun `formatLogMessage includes timestamp level tag and message`() {
        // Act
        val result = formatLogMessage(logMessage())

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | I | Tag | message", result)
    }

    @Test
    fun `formatLogMessage omits the tag segment for an empty tag`() {
        // Act
        val result = formatLogMessage(logMessage(tag = ""))

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | I | message", result)
    }

    @Test
    fun `formatLogMessage omits the tag segment for a blank tag`() {
        // Act
        val result = formatLogMessage(logMessage(tag = "   "))

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | I | message", result)
    }

    @Test
    fun `formatLogMessage uses the level label`() {
        // Act
        val debug = formatLogMessage(logMessage(level = LogLevel.DEBUG))
        val warning = formatLogMessage(logMessage(level = LogLevel.WARNING))
        val error = formatLogMessage(logMessage(level = LogLevel.ERROR))

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | D | Tag | message", debug)
        assertEquals("${TIMESTAMP.timeString()} | W | Tag | message", warning)
        assertEquals("${TIMESTAMP.timeString()} | E | Tag | message", error)
    }

    @Test
    fun `formatLogMessage keeps the message verbatim`() {
        // Arrange
        val message = "line 1\n\tline 2"

        // Act
        val result = formatLogMessage(logMessage(message = message))

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | I | Tag | $message", result)
    }

    @Test
    fun `formatLogMessage uses the provided timestamp`() {
        // Arrange
        val otherTimestamp = TIMESTAMP + 3_600_000L

        // Act
        val result = formatLogMessage(logMessage(timestamp = otherTimestamp))

        // Assert
        assertEquals("${otherTimestamp.timeString()} | I | Tag | message", result)
    }
}
