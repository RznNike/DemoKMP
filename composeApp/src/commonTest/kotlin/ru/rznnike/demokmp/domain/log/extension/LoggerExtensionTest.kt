package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val FIRST_CLOCK_MILLIS = 1_700_000_000_000L
private const val SECOND_CLOCK_MILLIS = 1_800_000_000_000L

private val fixedClock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)

@OptIn(ExperimentalCoroutinesApi::class)
class LoggerExtensionTest {
    private data class RecordedCall(
        val tag: String,
        val message: String,
        val level: LogLevel,
        val type: LogType
    )

    private class RecordingLoggerExtension(
        stopAfterOneError: Boolean = false,
        isDebugEnabled: Boolean = BuildKonfig.DEBUG
    ) : LoggerExtension(
        stopAfterOneError = stopAfterOneError,
        isDebugEnabled = isDebugEnabled
    ) {
        val recorded = mutableListOf<RecordedCall>()
        val readClockMillis = mutableListOf<Long>()
        val readDispatchers = mutableListOf<CoroutineDispatcher>()

        override suspend fun addMessage(
            tag: String,
            message: String,
            level: LogLevel,
            type: LogType,
            callback: suspend (LogMessage) -> Unit
        ) {
            readClockMillis += clock.millis()
            readDispatchers += coroutineDispatcher
            recorded += RecordedCall(tag = tag, message = message, level = level, type = type)
        }
    }

    private class StateProbeLoggerExtension(
        stopAfterOneError: Boolean = false
    ) : LoggerExtension(stopAfterOneError = stopAfterOneError) {
        val stopAfterOneErrorValue get() = stopAfterOneError
        val isErrorDetectedValue get() = isErrorDetected

        override suspend fun addMessage(
            tag: String,
            message: String,
            level: LogLevel,
            type: LogType,
            callback: suspend (LogMessage) -> Unit
        ) = Unit
    }

    private fun TestScope.createExtension(
        stopAfterOneError: Boolean = false,
        isDebugEnabled: Boolean = BuildKonfig.DEBUG
    ) = RecordingLoggerExtension(
        stopAfterOneError = stopAfterOneError,
        isDebugEnabled = isDebugEnabled
    ).apply {
        init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
        )
    }

    @Test
    fun `init replaces a previously injected clock and dispatcher`() = runTest {
        // Arrange
        val firstDispatcher = UnconfinedTestDispatcher(testScheduler)
        val secondDispatcher = UnconfinedTestDispatcher(testScheduler)
        val extension = RecordingLoggerExtension()

        // Act
        extension.init(
            clock = Clock.fixed(Instant.ofEpochMilli(FIRST_CLOCK_MILLIS), ZoneOffset.UTC),
            coroutineDispatcher = firstDispatcher
        )
        extension.i(tag = "Tag", message = "first")
        // Logger.init and Logger.addExtension both call init() on an already initialized extension
        extension.init(
            clock = Clock.fixed(Instant.ofEpochMilli(SECOND_CLOCK_MILLIS), ZoneOffset.UTC),
            coroutineDispatcher = secondDispatcher
        )
        extension.i(tag = "Tag", message = "second")

        // Assert
        assertEquals(listOf(FIRST_CLOCK_MILLIS, SECOND_CLOCK_MILLIS), extension.readClockMillis)
        assertEquals(
            listOf<CoroutineDispatcher>(firstDispatcher, secondDispatcher),
            extension.readDispatchers
        )
    }

    @Test
    fun `the extension cannot be used before init`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()

        // Act / Assert
        assertFailsWith<UninitializedPropertyAccessException> {
            extension.i(tag = "Tag", message = "info")
        }
        assertTrue(extension.recorded.isEmpty())
    }

    @Test
    fun `level focused methods map to expected level and type`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()

        // Act
        extension.i(tag = "Tag", message = "info")
        extension.w(tag = "Tag", message = "warning")
        extension.e(tag = "Tag", message = "error")
        extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")
        extension.networkResponse(
            tag = "Tag",
            requestUuid = uuid,
            message = "response",
            state = NetworkRequestState.SUCCESS
        )

        // Assert
        assertEquals(
            listOf(
                RecordedCall(tag = "Tag", message = "info", level = LogLevel.INFO, type = LogType.DEFAULT),
                RecordedCall(tag = "Tag", message = "warning", level = LogLevel.WARNING, type = LogType.DEFAULT),
                RecordedCall(tag = "Tag", message = "error", level = LogLevel.ERROR, type = LogType.DEFAULT),
                RecordedCall(tag = "Tag", message = "request", level = LogLevel.INFO, type = LogType.NETWORK),
                RecordedCall(tag = "Tag", message = "response", level = LogLevel.INFO, type = LogType.NETWORK)
            ),
            extension.recorded
        )
    }

    @Test
    fun `e with exception passes the message with the appended stack trace`() = runTest {
        // Arrange
        val extension = createExtension()
        val exception = IllegalStateException("boom")

        // Act
        extension.e(tag = "Tag", exception = exception, message = "failed")

        // Assert
        assertEquals(
            listOf(
                RecordedCall(
                    tag = "Tag",
                    message = "failed\n${exception.stackTraceToString()}",
                    level = LogLevel.ERROR,
                    type = LogType.DEFAULT
                )
            ),
            extension.recorded
        )
    }

    @Test
    fun `network helpers drop the curl and the request state`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()
        val expected = RecordedCall(
            tag = "Tag",
            message = "request",
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )

        // Act
        extension.networkRequest(
            tag = "Tag",
            uuid = uuid,
            message = "request",
            curl = "curl -H 'Authorization: Bearer secret-token' https://example.com"
        )
        extension.networkResponse(
            tag = "Tag",
            requestUuid = uuid,
            message = "request",
            state = NetworkRequestState.ERROR
        )

        // Assert
        // The base forwards neither the curl nor the state: subclasses override these helpers to use them
        assertEquals(listOf(expected, expected), extension.recorded)
    }

    @Test
    fun `d forwards a debug message when debug is enabled`() = runTest {
        // Arrange
        val extension = createExtension(isDebugEnabled = true)

        // Act
        extension.d(tag = "Tag", message = "debug text")

        // Assert
        assertEquals(
            listOf(
                RecordedCall(tag = "Tag", message = "debug text", level = LogLevel.DEBUG, type = LogType.DEFAULT)
            ),
            extension.recorded
        )
    }

    @Test
    fun `d drops the debug message when debug is disabled`() = runTest {
        // Arrange
        val extension = createExtension(isDebugEnabled = false)

        // Act
        extension.d(tag = "Tag", message = "debug text")

        // Assert
        assertTrue(extension.recorded.isEmpty())
    }

    @Test
    fun `the debug gate defaults to BuildKonfig_DEBUG`() = runTest {
        // Arrange
        val extension = createExtension()

        // Act
        extension.d(tag = "Tag", message = "debug text")

        // Assert
        assertEquals(
            if (BuildKonfig.DEBUG) {
                listOf(
                    RecordedCall(tag = "Tag", message = "debug text", level = LogLevel.DEBUG, type = LogType.DEFAULT)
                )
            } else {
                emptyList()
            },
            extension.recorded
        )
    }

    @Test
    fun `protected state has the expected defaults`() {
        // Assert: the gate in ConsoleLoggerExtension/DatabaseLoggerExtension relies on both of these
        assertFalse(StateProbeLoggerExtension().stopAfterOneErrorValue)
        assertFalse(StateProbeLoggerExtension().isErrorDetectedValue)
        assertTrue(StateProbeLoggerExtension(stopAfterOneError = true).stopAfterOneErrorValue)
    }
}
