package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val TIMESTAMP_MILLIS = 1_700_000_000_000L

private val fixedClock = Clock.fixed(Instant.ofEpochMilli(TIMESTAMP_MILLIS), ZoneOffset.UTC)

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryCacheLoggerExtensionTest {
    /**
     * Captures what a subscription reports. The buffer snapshot handed to `initCallback` is a
     * reference to the extension's live list, so it grows with the test.
     */
    private class LogSubscription {
        val initial = mutableListOf<List<LogMessage>>()
        val updates = mutableListOf<LogMessage>()

        val buffered: List<LogMessage>
            get() = assertNotNull(initial.lastOrNull(), "initCallback was never invoked")
    }

    private class NetworkLogSubscription {
        val initial = mutableListOf<List<NetworkLogMessage>>()
        val updates = mutableListOf<NetworkLogMessage>()

        val buffered: List<NetworkLogMessage>
            get() = assertNotNull(initial.lastOrNull(), "initCallback was never invoked")
    }

    private fun TestScope.createExtension(
        stopAfterOneError: Boolean = false
    ) = MemoryCacheLoggerExtension(
        stopAfterOneError = stopAfterOneError
    ).apply {
        init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler)
        )
    }

    /**
     * Runs [block] with a live log subscription.
     *
     * `subscribeToLog` suspends forever in `collect`, so the collector is started as a child job and
     * cancelled on the way out. Without the cancellation `runTest` would wait for it until its timeout.
     * `UnconfinedTestDispatcher` runs the body eagerly, so `initCallback` has already fired on return.
     */
    private suspend fun TestScope.withLogSubscription(
        extension: MemoryCacheLoggerExtension,
        block: suspend (LogSubscription) -> Unit
    ) {
        val subscription = LogSubscription()
        val job: Job = launch(UnconfinedTestDispatcher(testScheduler)) {
            extension.subscribeToLog(
                initCallback = { subscription.initial += it },
                updateCallback = { subscription.updates += it }
            )
        }
        try {
            block(subscription)
        } finally {
            job.cancel()
        }
    }

    /** Bounded network log subscription counterpart of [withLogSubscription]. */
    private suspend fun TestScope.withNetworkLogSubscription(
        extension: MemoryCacheLoggerExtension,
        block: suspend (NetworkLogSubscription) -> Unit
    ) {
        val subscription = NetworkLogSubscription()
        val job: Job = launch(UnconfinedTestDispatcher(testScheduler)) {
            extension.subscribeToNetworkLog(
                initCallback = { subscription.initial += it },
                updateCallback = { subscription.updates += it }
            )
        }
        try {
            block(subscription)
        } finally {
            job.cancel()
        }
    }

    private fun logMessage(
        type: LogType = LogType.DEFAULT,
        level: LogLevel = LogLevel.INFO,
        timestamp: Long = TIMESTAMP_MILLIS,
        tag: String = "Tag",
        message: String = "message"
    ) = LogMessage(
        type = type,
        level = level,
        timestamp = timestamp,
        tag = tag,
        message = message
    )

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
                logMessage(type = LogType.NETWORK, message = "explicit type"),
                logMessage(level = LogLevel.WARNING, tag = "OtherTag", message = "default type")
            ),
            messages
        )
    }

    @Test
    fun `subscribeToLog hands over the current buffer and streams new messages`() = runTest {
        // Arrange
        val extension = createExtension()
        extension.addMessage(tag = "Tag", message = "before", level = LogLevel.INFO)

        // Act / Assert
        withLogSubscription(extension) { subscription ->
            extension.addMessage(tag = "Tag", message = "after", level = LogLevel.INFO)
            advanceUntilIdle()

            assertEquals(listOf("before", "after"), subscription.buffered.map { it.message })
            assertEquals(listOf("after"), subscription.updates.map { it.message })
        }
    }

    @Test
    fun `subscribeToLog starts from an empty buffer when nothing was logged`() = runTest {
        // Arrange
        val extension = createExtension()

        // Act / Assert: the pre-subscription buffer is empty, the update arrives over the flow
        withLogSubscription(extension) { subscription ->
            assertTrue(subscription.buffered.isEmpty())

            extension.addMessage(tag = "Tag", message = "while subscribed", level = LogLevel.INFO)
            advanceUntilIdle()

            assertEquals(listOf("while subscribed"), subscription.updates.map { it.message })
        }
    }

    @Test
    fun `subscribeToLog exposes the live buffer instance`() = runTest {
        // Arrange
        val extension = createExtension()
        extension.addMessage(tag = "Tag", message = "first", level = LogLevel.INFO)

        // Act / Assert: the extension keeps a single list, so the earlier snapshot grows too
        withLogSubscription(extension) { subscription ->
            extension.addMessage(tag = "Tag", message = "second", level = LogLevel.INFO)
            advanceUntilIdle()

            assertEquals(listOf("first", "second"), subscription.buffered.map { it.message })
        }
    }

    @Test
    fun `networkRequest stores the message with uuid and curl and starts as SENT`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()

        // Act / Assert
        withNetworkLogSubscription(extension) { subscription ->
            extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")
            advanceUntilIdle()

            val message = subscription.buffered.single()
            assertEquals(uuid, message.uuid)
            assertEquals("curl", message.curl)
            assertEquals(NetworkRequestState.SENT, message.state)
            assertEquals(logMessage(type = LogType.NETWORK, message = "request"), message.request)
        }
    }

    @Test
    fun `networkResponse pairs the response with the request and marks it successful`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()
        extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")

        // Act / Assert
        withNetworkLogSubscription(extension) { subscription ->
            extension.networkResponse(
                tag = "Tag",
                requestUuid = uuid,
                message = "response",
                state = NetworkRequestState.SENT
            )
            advanceUntilIdle()

            val updated = subscription.buffered.single()
            assertEquals(uuid, updated.uuid)
            assertEquals(NetworkRequestState.SUCCESS, updated.state)
            assertEquals("request", updated.request.message)
            assertEquals(logMessage(type = LogType.NETWORK, message = "response"), updated.response)
            assertEquals(
                listOf(NetworkRequestState.SUCCESS),
                subscription.updates.map { it.state }
            )
        }
    }

    @Test
    fun `networkResponse keeps a failure state as is`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()
        extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")

        // Act / Assert
        withNetworkLogSubscription(extension) { subscription ->
            extension.networkResponse(
                tag = "Tag",
                requestUuid = uuid,
                message = "response",
                state = NetworkRequestState.ERROR
            )
            advanceUntilIdle()

            assertEquals(NetworkRequestState.ERROR, subscription.buffered.single().state)
        }
    }

    @Test
    fun `networkResponse ignores an unknown uuid`() = runTest {
        // Arrange
        val extension = createExtension()
        extension.networkRequest(
            tag = "Tag",
            uuid = UUID.randomUUID(),
            message = "request",
            curl = "curl"
        )

        // Act / Assert: nothing is emitted and the unmatched request keeps a null response
        withNetworkLogSubscription(extension) { subscription ->
            extension.networkResponse(
                tag = "Tag",
                requestUuid = UUID.randomUUID(),
                message = "response",
                state = NetworkRequestState.SUCCESS
            )
            advanceUntilIdle()

            assertTrue(subscription.updates.isEmpty())
            val stored = subscription.buffered.single()
            assertNull(stored.response)
            assertEquals(NetworkRequestState.SENT, stored.state)
        }
    }

    @Test
    fun `clearLog empties the buffer`() = runTest {
        // Arrange
        val extension = createExtension()
        extension.addMessage(tag = "Tag", message = "first", level = LogLevel.INFO)
        extension.addMessage(tag = "Tag", message = "second", level = LogLevel.INFO)

        // Act
        extension.clearLog()

        // Assert
        withLogSubscription(extension) { subscription ->
            assertTrue(subscription.buffered.isEmpty())
        }
    }

    @Test
    fun `clearNetworkLog empties the buffer`() = runTest {
        // Arrange
        val extension = createExtension()
        extension.networkRequest(
            tag = "Tag",
            uuid = UUID.randomUUID(),
            message = "request",
            curl = "curl"
        )

        // Act
        extension.clearNetworkLog()

        // Assert
        withNetworkLogSubscription(extension) { subscription ->
            assertTrue(subscription.buffered.isEmpty())
        }
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

    @Test
    fun `a message is buffered before the callback fails`() = runTest {
        // Arrange
        val extension = createExtension()
        val failingCallback: suspend (LogMessage) -> Unit = {
            throw IllegalStateException("callback failed")
        }

        // Act
        assertFailsWith<IllegalStateException> {
            extension.addMessage(
                tag = "Tag",
                message = "buffered",
                level = LogLevel.INFO,
                callback = failingCallback
            )
        }

        // Assert: the buffer is written under the lock before the callback runs
        withLogSubscription(extension) { subscription ->
            assertEquals(listOf("buffered"), subscription.buffered.map { it.message })
        }
    }

    @Test
    fun `networkResponse replaces the entry in place instead of appending`() = runTest {
        // Arrange
        val extension = createExtension()
        val uuid = UUID.randomUUID()

        // Act
        extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")
        extension.networkResponse(
            tag = "Tag",
            requestUuid = uuid,
            message = "response",
            state = NetworkRequestState.TIMEOUT
        )

        // Assert
        withNetworkLogSubscription(extension) { subscription ->
            val buffered = subscription.buffered
            assertEquals(1, buffered.size)
            assertEquals(NetworkRequestState.TIMEOUT, buffered.single().state)
            assertNotNull(buffered.single().response)
        }
    }

    @Test
    fun `concurrent messages are all buffered without loss`() = runTest {
        // Arrange
        val extension = createExtension()
        val messageCount = 20

        // Act
        val jobs = (0 until messageCount).map { index ->
            launch {
                extension.addMessage(tag = "Tag", message = "message $index", level = LogLevel.INFO)
            }
        }
        jobs.joinAll()

        // Assert
        withLogSubscription(extension) { subscription ->
            val buffered = subscription.buffered
            assertEquals(messageCount, buffered.size)
            assertEquals(
                (0 until messageCount).map { "message $it" }.toSet(),
                buffered.map { it.message }.toSet()
            )
        }
    }
}
