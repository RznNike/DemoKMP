package ru.rznnike.demokmp.domain.log

import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val TIMESTAMP = 1_700_000_000_000L

private fun Long.timeString(): String = toDateString(GlobalConstants.DATE_PATTERN_TIME_MS)

private fun logMessage(
    level: LogLevel = LogLevel.INFO,
    tag: String = "Tag",
    message: String = "message"
) = LogMessage(
    type = LogType.DEFAULT,
    level = level,
    timestamp = TIMESTAMP,
    tag = tag,
    message = message
)

class PrintLogTest {
    private val originalOut = System.out
    private val originalErr = System.err
    private val outBuffer = ByteArrayOutputStream()
    private val errBuffer = ByteArrayOutputStream()

    @BeforeTest
    fun setUp() {
        outBuffer.reset()
        errBuffer.reset()
        System.setOut(PrintStream(outBuffer, true))
        System.setErr(PrintStream(errBuffer, true))
    }

    @AfterTest
    fun tearDown() {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }

    @Test
    fun `printLog writes a non error message to stdout`() {
        // Arrange
        val message = logMessage(level = LogLevel.INFO, message = "hello")

        // Act
        printLog(message)

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | I | Tag | hello", outBuffer.toString().trim())
    }

    @Test
    fun `printLog writes an error message to stderr`() {
        // Arrange
        val message = logMessage(level = LogLevel.ERROR, message = "boom")

        // Act
        printLog(message)

        // Assert
        assertEquals("${TIMESTAMP.timeString()} | E | Tag | boom", errBuffer.toString().trim())
    }

    @Test
    fun `printLog does not write an error message to stdout`() {
        // Arrange
        val message = logMessage(level = LogLevel.ERROR)

        // Act
        printLog(message)

        // Assert
        assertTrue(outBuffer.toString().isEmpty())
    }

    @Test
    fun `printLog does not write a non error message to stderr`() {
        // Arrange
        val message = logMessage(level = LogLevel.WARNING)

        // Act
        printLog(message)

        // Assert
        assertTrue(errBuffer.toString().isEmpty())
    }
}
