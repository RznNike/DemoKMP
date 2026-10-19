package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

private const val TIMESTAMP_MILLIS = 1_700_000_000_000L

private val fixedClock = Clock.fixed(Instant.ofEpochMilli(TIMESTAMP_MILLIS), ZoneOffset.UTC)

@OptIn(ExperimentalCoroutinesApi::class)
class ConsoleLoggerExtensionTest {
    private fun TestScope.createExtension(
        stopAfterOneError: Boolean = false
    ) = ConsoleLoggerExtension(stopAfterOneError = stopAfterOneError).apply {
        init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
        )
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
                    timestamp = TIMESTAMP_MILLIS,
                    tag = "Tag",
                    message = "explicit type"
                ),
                LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.WARNING,
                    timestamp = TIMESTAMP_MILLIS,
                    tag = "OtherTag",
                    message = "default type"
                )
            ),
            messages
        )
    }

    @Test
    fun `a failing callback propagates the error and the extension keeps working`() = runTest {
        // Arrange
        val messages = mutableListOf<LogMessage>()
        val failure = IllegalStateException("callback failed")
        var shouldFail = true
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
}
