package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.formatLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.millis
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.domain.utils.toLocalDate
import java.io.File
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val INITIAL_MILLIS = 1_700_000_000_000L
// +30h guarantees a different local date in every time zone, including DST transitions
private const val NEXT_DAY_MILLIS = INITIAL_MILLIS + 30 * 60 * 60 * 1000L

private class MutableClock(var currentMillis: Long) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId?): Clock = this
    override fun instant(): Instant = Instant.ofEpochMilli(currentMillis)
}

private fun logFileNameAt(millis: Long): String =
    "${millis.toLocalDate().millis().toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_DAY)}.txt"

@OptIn(ExperimentalCoroutinesApi::class)
class FileLoggerExtensionTest {
    private val tempDirs = mutableListOf<File>()
    private val clock = MutableClock(INITIAL_MILLIS)

    private fun createTempDir(): File =
        Files.createTempDirectory("demokmp-file-logger").toFile().also { tempDirs += it }

    private fun TestScope.createExtension(
        stopAfterOneError: Boolean = false,
        logsPath: File = createTempDir().resolve("logs")
    ) = FileLoggerExtension(stopAfterOneError = stopAfterOneError, logsPath = logsPath.path).apply {
        init(
            clock = clock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
        )
    }

    @AfterTest
    fun tearDown() {
        tempDirs.forEach { it.deleteRecursively() }
        tempDirs.clear()
    }

    @Test
    fun `addMessage creates the logs directory and writes the formatted line to the dated file`() = runTest {
        // Arrange
        val logsDir = createTempDir().resolve("nested/logs")
        val extension = createExtension(logsPath = logsDir)
        val sent = LogMessage(
            type = LogType.DEFAULT,
            level = LogLevel.INFO,
            timestamp = INITIAL_MILLIS,
            tag = "Tag",
            message = "first"
        )

        // Act
        extension.addMessage(tag = "Tag", message = "first", level = LogLevel.INFO)

        // Assert
        val logFile = File(logsDir, logFileNameAt(INITIAL_MILLIS))
        assertTrue(logsDir.isDirectory, "logs directory should be created on demand")
        assertEquals(formatLogMessage(sent) + "\n", logFile.readText())
    }

    @Test
    fun `consecutive messages append to a single dated file`() = runTest {
        // Arrange
        val logsDir = createTempDir().resolve("logs")
        val extension = createExtension(logsPath = logsDir)

        // Act
        extension.addMessage(tag = "Tag", message = "first", level = LogLevel.INFO)
        extension.addMessage(tag = "Tag", message = "second", level = LogLevel.WARNING)

        // Assert
        val logFile = File(logsDir, logFileNameAt(INITIAL_MILLIS))
        assertEquals(
            listOf("first", "second"),
            logFile.readLines().map { it.substringAfterLast(" | ") }
        )
        assertEquals(1, logsDir.listFiles()?.size, "same day should reuse a single file")
    }

    @Test
    fun `addMessage builds the message and forwards it to the callback`() = runTest {
        // Arrange
        val messages = mutableListOf<LogMessage>()
        val extension = createExtension()

        // Act
        extension.addMessage(
            tag = "Tag",
            message = "explicit type",
            level = LogLevel.INFO,
            type = LogType.NETWORK,
            callback = { messages += it }
        )
        extension.addMessage(
            tag = "OtherTag",
            message = "default type",
            level = LogLevel.WARNING,
            callback = { messages += it }
        )

        // Assert
        assertEquals(
            listOf(
                LogMessage(
                    type = LogType.NETWORK,
                    level = LogLevel.INFO,
                    timestamp = INITIAL_MILLIS,
                    tag = "Tag",
                    message = "explicit type"
                ),
                LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.WARNING,
                    timestamp = INITIAL_MILLIS,
                    tag = "OtherTag",
                    message = "default type"
                )
            ),
            messages
        )
    }

    @Test
    fun `a new day switches to a new dated file`() = runTest {
        // Arrange
        val logsDir = createTempDir().resolve("logs")
        val extension = createExtension(logsPath = logsDir)

        // Act
        extension.addMessage(tag = "Tag", message = "day one", level = LogLevel.INFO)
        clock.currentMillis = NEXT_DAY_MILLIS
        extension.addMessage(tag = "Tag", message = "day two", level = LogLevel.INFO)

        // Assert
        val firstFile = File(logsDir, logFileNameAt(INITIAL_MILLIS))
        val secondFile = File(logsDir, logFileNameAt(NEXT_DAY_MILLIS))
        assertTrue(firstFile.readText().endsWith("day one\n"), "previous day file keeps its content")
        assertTrue(secondFile.readText().endsWith("day two\n"), "new day starts a new file")
    }

    @Test
    fun `a failed file write is swallowed and the callback still runs`() = runTest {
        // Arrange: a regular file where the logs directory should be makes every append fail
        val logsPath = createTempDir().resolve("logs").apply { writeText("not a directory") }
        val extension = createExtension(logsPath = logsPath)
        val messages = mutableListOf<LogMessage>()

        // Act: no exception expected, the inner catch swallows the write error
        extension.addMessage(
            tag = "Tag",
            message = "message",
            level = LogLevel.ERROR,
            callback = { messages += it }
        )

        // Assert
        assertEquals(
            listOf(
                LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.ERROR,
                    timestamp = INITIAL_MILLIS,
                    tag = "Tag",
                    message = "message"
                )
            ),
            messages
        )
    }

    @Test
    fun `a failing callback propagates the error and the extension keeps working`() = runTest {
        // Arrange
        val failure = IllegalStateException("callback failed")
        var shouldFail = true
        val messages = mutableListOf<LogMessage>()
        val extension = createExtension(stopAfterOneError = false)
        val failingCallback: suspend (LogMessage) -> Unit = { message ->
            if (shouldFail) {
                shouldFail = false
                throw failure
            }
            messages += message
        }

        // Act
        val thrown = assertFailsWith<IllegalStateException> {
            extension.addMessage(
                tag = "Tag",
                message = "first",
                level = LogLevel.INFO,
                callback = failingCallback
            )
        }
        extension.addMessage(
            tag = "Tag",
            message = "second",
            level = LogLevel.INFO,
            callback = { messages += it }
        )

        // Assert
        assertSame(failure, thrown)
        assertEquals(listOf("second"), messages.map { it.message })
    }

    @Test
    fun `stopAfterOneError drops every message after the first failure`() = runTest {
        // Arrange
        var callbackCalls = 0
        val extension = createExtension(stopAfterOneError = true)
        val failingCallback: suspend (LogMessage) -> Unit = {
            callbackCalls++
            throw IllegalStateException("callback failed")
        }

        // Act
        assertFailsWith<IllegalStateException> {
            extension.addMessage(
                tag = "Tag",
                message = "first",
                level = LogLevel.INFO,
                callback = failingCallback
            )
        }
        // No exception expected here: the isErrorDetected latch drops the message
        extension.addMessage(
            tag = "Tag",
            message = "second",
            level = LogLevel.INFO,
            callback = failingCallback
        )

        // Assert
        assertEquals(1, callbackCalls)
    }

    @Test
    fun `concurrent messages are all written without loss`() = runTest {
        // Arrange
        val logsDir = createTempDir().resolve("logs")
        val extension = createExtension(logsPath = logsDir)
        val messageCount = 20

        // Act
        val jobs = (0 until messageCount).map { index ->
            launch {
                extension.addMessage(tag = "Tag", message = "message $index", level = LogLevel.INFO)
            }
        }
        jobs.joinAll()

        // Assert
        val lines = File(logsDir, logFileNameAt(INITIAL_MILLIS)).readLines()
        assertEquals(messageCount, lines.size)
        assertEquals(
            (0 until messageCount).map { "message $it" }.toSet(),
            lines.map { it.substringAfterLast(" | ") }.toSet()
        )
    }
}
