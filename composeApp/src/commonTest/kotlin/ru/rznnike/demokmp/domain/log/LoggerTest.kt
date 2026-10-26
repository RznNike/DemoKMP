package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.log.extension.LoggerExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private const val CLOCK_MILLIS = 1_700_000_000_000L

private val fixedClock = Clock.fixed(Instant.ofEpochMilli(CLOCK_MILLIS), ZoneOffset.UTC)

private data class RecordedCall(
    val tag: String,
    val message: String,
    val level: LogLevel,
    val type: LogType
)

private data class RecordedNetworkRequest(
    val tag: String,
    val uuid: UUID,
    val message: String,
    val curl: String
)

private data class RecordedNetworkResponse(
    val tag: String,
    val requestUuid: UUID,
    val message: String,
    val state: NetworkRequestState
)

/**
 * Captures everything the logger hands over to an extension. The base [LoggerExtension] drops the
 * uuid, the curl and the request state, so the network helpers are overridden to keep them.
 */
private class RecordingLoggerExtension(
    isDebugEnabled: Boolean = true
) : LoggerExtension(isDebugEnabled = isDebugEnabled) {
    val calls = mutableListOf<RecordedCall>()
    val networkRequests = mutableListOf<RecordedNetworkRequest>()
    val networkResponses = mutableListOf<RecordedNetworkResponse>()
    val initializedClocks = mutableListOf<Clock>()
    val initializedDispatchers = mutableListOf<CoroutineDispatcher>()

    var initFailure: Exception? = null
    var loggingFailure: Exception? = null

    override fun init(clock: Clock, coroutineDispatcher: CoroutineDispatcher) {
        initFailure?.let { throw it }
        super.init(clock, coroutineDispatcher)
        initializedClocks += clock
        initializedDispatchers += coroutineDispatcher
    }

    override suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        timestamp: Long,
        callback: suspend (LogMessage) -> Unit
    ) {
        loggingFailure?.let { throw it }
        calls += RecordedCall(tag = tag, message = message, level = level, type = type)
    }

    override suspend fun networkRequest(
        tag: String,
        uuid: UUID,
        message: String,
        curl: String,
        timestamp: Long
    ) {
        loggingFailure?.let { throw it }
        networkRequests += RecordedNetworkRequest(tag = tag, uuid = uuid, message = message, curl = curl)
    }

    override suspend fun networkResponse(
        tag: String,
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState,
        timestamp: Long
    ) {
        loggingFailure?.let { throw it }
        networkResponses += RecordedNetworkResponse(
            tag = tag,
            requestUuid = requestUuid,
            message = message,
            state = state
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoggerTest {
    private fun TestScope.initLogger(extensions: List<LoggerExtension>) {
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler),
            extensions = extensions
        )
    }

    @Test
    fun `init initializes every extension with the given clock and dispatcher`() = runTest {
        // Arrange
        val first = RecordingLoggerExtension()
        val second = RecordingLoggerExtension()
        val dispatcher = UnconfinedTestDispatcher(testScheduler)

        // Act
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = dispatcher,
            extensions = listOf(first, second)
        )

        // Assert
        assertEquals(listOf(fixedClock), first.initializedClocks)
        assertEquals(listOf(fixedClock), second.initializedClocks)
        assertEquals(listOf<CoroutineDispatcher>(dispatcher), first.initializedDispatchers)
        assertEquals(listOf<CoroutineDispatcher>(dispatcher), second.initializedDispatchers)
    }

    @Test
    fun `init keeps initializing the remaining extensions when one fails`() = runTest {
        // Arrange
        val failing = RecordingLoggerExtension().apply {
            initFailure = IllegalStateException("init failed")
        }
        val working = RecordingLoggerExtension()

        // Act
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler),
            extensions = listOf(failing, working)
        )

        // Assert
        assertTrue(failing.initializedClocks.isEmpty())
        assertEquals(listOf(fixedClock), working.initializedClocks)
    }

    @Test
    fun `init replaces the previously registered extensions`() = runTest {
        // Arrange
        val first = RecordingLoggerExtension()
        val second = RecordingLoggerExtension()
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler),
            extensions = listOf(first)
        )

        // Act
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler),
            extensions = listOf(second)
        )
        Logger.i("message")
        advanceUntilIdle()

        // Assert
        assertTrue(first.calls.isEmpty())
        assertEquals(listOf("message"), second.calls.map { it.message })
    }

    @Test
    fun `addExtension registers and initializes the extension`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = emptyList())

        // Act
        Logger.addExtension(extension)
        Logger.i("message")
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(fixedClock), extension.initializedClocks)
        assertEquals(listOf("message"), extension.calls.map { it.message })
    }

    @Test
    fun `addExtension does not register an extension whose init fails`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension().apply {
            initFailure = IllegalStateException("init failed")
        }
        initLogger(extensions = emptyList())

        // Act
        Logger.addExtension(extension)
        Logger.i("message")
        advanceUntilIdle()

        // Assert
        assertTrue(extension.calls.isEmpty())
    }

    @Test
    fun `withTag forwards the tag to the extensions`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        Logger.withTag("MyTag").i("message")
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("MyTag"), extension.calls.map { it.tag })
    }

    @Test
    fun `the default logger uses an empty tag`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        Logger.i("message")
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(""), extension.calls.map { it.tag })
    }

    @Test
    fun `level focused methods map to the expected level`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))
        val logger = Logger.withTag("Tag")

        // Act
        logger.d("debug")
        logger.i("info")
        logger.w("warning")
        logger.e("error")
        advanceUntilIdle()

        // Assert
        assertEquals(
            listOf(LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARNING, LogLevel.ERROR),
            extension.calls.map { it.level }
        )
    }

    @Test
    fun `level focused methods use the default log type`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        Logger.withTag("Tag").i("message")
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(LogType.DEFAULT), extension.calls.map { it.type })
    }

    @Test
    fun `e with an exception forwards the message with the appended stack trace`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))
        val exception = IllegalStateException("boom")

        // Act
        Logger.withTag("Tag").e(exception, "failed")
        advanceUntilIdle()

        // Assert
        assertEquals("failed\n${exception.stackTraceToString()}", extension.calls.single().message)
    }

    @Test
    fun `networkRequest returns the uuid that is forwarded to the extension`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        val uuid = Logger.withTag("Tag").networkRequest(message = "request", curl = "curl command")
        advanceUntilIdle()

        // Assert
        assertEquals(uuid, extension.networkRequests.single().uuid)
    }

    @Test
    fun `networkRequest forwards the message and the curl to the extension`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        Logger.withTag("Tag").networkRequest(message = "request", curl = "curl command")
        advanceUntilIdle()

        // Assert
        assertEquals("request", extension.networkRequests.single().message)
        assertEquals("curl command", extension.networkRequests.single().curl)
    }

    @Test
    fun `networkRequest returns a distinct uuid for each call`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))

        // Act
        val first = Logger.networkRequest(message = "request", curl = "curl")
        val second = Logger.networkRequest(message = "request", curl = "curl")
        advanceUntilIdle()

        // Assert
        assertNotEquals(first, second)
    }

    @Test
    fun `networkResponse forwards the request uuid and the state to the extension`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extensions = listOf(extension))
        val uuid = UUID.randomUUID()

        // Act
        Logger.withTag("Tag").networkResponse(
            requestUuid = uuid,
            message = "response",
            state = NetworkRequestState.SUCCESS
        )
        advanceUntilIdle()

        // Assert
        assertEquals(uuid, extension.networkResponses.single().requestUuid)
        assertEquals(NetworkRequestState.SUCCESS, extension.networkResponses.single().state)
    }

    @Test
    fun `an extension that fails does not prevent the other extensions from receiving the message`() = runTest {
        // Arrange
        val failing = RecordingLoggerExtension().apply {
            loggingFailure = IllegalStateException("logging failed")
        }
        val working = RecordingLoggerExtension()
        initLogger(extensions = listOf(failing, working))

        // Act
        Logger.withTag("Tag").i("message")
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("message"), working.calls.map { it.message })
    }

    @Test
    fun `logging before the logger is initialized does not reach any extension`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()

        // Act
        Logger.withTag("Tag").i("message")
        advanceUntilIdle()

        // Assert
        assertTrue(extension.calls.isEmpty())
    }
}
