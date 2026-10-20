package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.interactor.log.AddLogMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogNetworkMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.DeleteOldLogsUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageUseCase
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import java.io.File
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val TIMESTAMP_MILLIS = 1_700_000_000_000L

private val fixedClock = Clock.fixed(Instant.ofEpochMilli(TIMESTAMP_MILLIS), ZoneOffset.UTC)

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseLoggerExtensionTest {
    /**
     * Records what the extension persists. Methods not under test fail loudly instead of
     * silently succeeding, so an unexpected call surfaces as a test failure.
     */
    private class FakeLogGateway : LogGateway {
        val storedLogMessages = mutableListOf<LogMessage>()
        val storedNetworkLogMessages = mutableListOf<NetworkLogMessage>()
        val deletedRetentionModes = mutableListOf<LogsRetentionMode>()

        /** Returned by [getLogNetworkMessage] when set; otherwise that call returns null. */
        var networkMessageToReturn: NetworkLogMessage? = null

        override suspend fun addLogMessageToDB(message: LogMessage) {
            storedLogMessages += message
        }

        override suspend fun addLogNetworkMessageToDB(message: NetworkLogMessage) {
            storedNetworkLogMessages += message
        }

        override suspend fun getLogNetworkMessage(uuid: UUID): NetworkLogMessage? =
            networkMessageToReturn?.takeIf { it.uuid == uuid }

        override suspend fun deleteOldLogs(logsRetentionMode: LogsRetentionMode) {
            deletedRetentionModes += logsRetentionMode
        }

        override suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<NetworkLogMessage?> =
            flowOf(getLogNetworkMessage(uuid))

        override suspend fun getLog(): LogData = notUsed("getLog")

        override suspend fun getNewLog(lastId: Long): List<LogMessage> = notUsed("getNewLog")

        override suspend fun getNetworkLog(): NetworkLogData = notUsed("getNetworkLog")

        override suspend fun getNewNetworkLog(lastId: Long): List<NetworkLogMessage> =
            notUsed("getNewNetworkLog")

        override suspend fun clearLog(): Unit = notUsed("clearLog")

        override suspend fun clearNetworkLog(): Unit = notUsed("clearNetworkLog")

        override suspend fun saveLogToFile(file: File): Unit = notUsed("saveLogToFile")

        override suspend fun saveNetworkLogMessageToFile(
            file: File,
            message: NetworkLogMessage
        ): Unit = notUsed("saveNetworkLogMessageToFile")

        private fun <T> notUsed(name: String): T =
            error("LogGateway.$name is not expected to be called by DatabaseLoggerExtension")
    }

    private lateinit var koinApplication: KoinApplication

    /** Transport for assertions shared with the Koin module. */
    private lateinit var gateway: FakeLogGateway

    @AfterTest
    fun tearDown() {
        if (::koinApplication.isInitialized) {
            stopKoin()
        }
    }

    /**
     * Wires the *real* use cases over a recording fake gateway.
     *
     * `DatabaseLoggerExtension` resolves its collaborators through `by inject()`, so the Koin
     * container is the injection mechanism — there is no constructor seam to use instead.
     */
    private fun startTestKoin(): FakeLogGateway {
        val fakeGateway = FakeLogGateway()
        gateway = fakeGateway
        koinApplication = startKoin {
            modules(
                module {
                    single<LogGateway> { fakeGateway }
                    single<DispatcherProvider> { FakeDispatcherProvider() }
                    single { AddLogMessageToDBUseCase(get(), get()) }
                    single { AddLogNetworkMessageToDBUseCase(get(), get()) }
                    single { GetLogNetworkMessageUseCase(get(), get()) }
                    single { DeleteOldLogsUseCase(get(), get()) }
                }
            )
        }
        return fakeGateway
    }

    /**
     * Creates the extension and runs its `init`, which persists a SESSION_START message.
     *
     * `init` calls `runBlocking { addMessage(...) }`, which hops onto [dispatcher]. An
     * unconfined dispatcher is required: a queued one would deadlock `runBlocking`.
     */
    private fun createExtension(
        dispatcher: TestDispatcher,
        stopAfterOneError: Boolean = false,
        logsRetentionMode: LogsRetentionMode = LogsRetentionMode.All
    ) = DatabaseLoggerExtension(
        stopAfterOneError = stopAfterOneError,
        logsRetentionMode = logsRetentionMode
    ).apply {
        init(clock = fixedClock, coroutineDispatcher = dispatcher)
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
    fun `init persists a session start message and its network wrapper`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        // Act
        createExtension(UnconfinedTestDispatcher(testScheduler))

        // Assert: the base message is stored as SESSION_START
        val sessionStart = fakeGateway.storedLogMessages.single()
        assertEquals(LogType.SESSION_START, sessionStart.type)
        assertEquals(LogLevel.INFO, sessionStart.level)
        assertEquals("", sessionStart.tag)
        assertEquals("", sessionStart.message)
        assertEquals(TIMESTAMP_MILLIS, sessionStart.timestamp)

        // Assert: the callback wraps it as a network message with a fresh uuid and no response
        val wrapper = fakeGateway.storedNetworkLogMessages.single()
        assertEquals(sessionStart, wrapper.request)
        assertNull(wrapper.response)
        assertEquals("", wrapper.curl)
    }

    @Test
    fun `init with All retention mode does not trigger a cleanup`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        // Act
        createExtension(UnconfinedTestDispatcher(testScheduler), logsRetentionMode = LogsRetentionMode.All)

        // Assert
        assertTrue(fakeGateway.deletedRetentionModes.isEmpty())
    }

    @Test
    fun `addMessage persists the message and forwards it to the callback`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedLogMessages.clear()
        val messages = mutableListOf<LogMessage>()

        // Act
        extension.addMessage(
            tag = "Tag",
            message = "explicit type",
            level = LogLevel.WARNING,
            type = LogType.NETWORK,
            callback = { messages += it }
        )

        // Assert
        val expected = logMessage(
            type = LogType.NETWORK,
            level = LogLevel.WARNING,
            message = "explicit type"
        )
        assertEquals(listOf(expected), fakeGateway.storedLogMessages)
        assertEquals(listOf(expected), messages)
    }

    @Test
    fun `addMessage defaults to the DEFAULT log type`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedLogMessages.clear()

        // Act
        extension.addMessage(tag = "Tag", message = "default type", level = LogLevel.ERROR)

        // Assert
        assertEquals(
            listOf(logMessage(level = LogLevel.ERROR, message = "default type")),
            fakeGateway.storedLogMessages
        )
    }

    @Test
    fun `networkRequest persists the message with uuid and curl and starts as SENT`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedLogMessages.clear()
        fakeGateway.storedNetworkLogMessages.clear()
        val uuid = UUID.randomUUID()

        // Act
        extension.networkRequest(tag = "Tag", uuid = uuid, message = "request", curl = "curl")

        // Assert
        // The base addMessage still records the plain message
        assertEquals(
            listOf(logMessage(type = LogType.NETWORK, message = "request")),
            fakeGateway.storedLogMessages
        )
        val stored = fakeGateway.storedNetworkLogMessages.single()
        assertEquals(uuid, stored.uuid)
        assertEquals("curl", stored.curl)
        assertEquals(NetworkRequestState.SENT, stored.state)
        assertEquals(logMessage(type = LogType.NETWORK, message = "request"), stored.request)
        assertNull(stored.response)
    }

    @Test
    fun `networkResponse pairs the stored message and marks SENT as SUCCESS`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedLogMessages.clear()
        fakeGateway.storedNetworkLogMessages.clear()
        val uuid = UUID.randomUUID()
        fakeGateway.networkMessageToReturn = NetworkLogMessage(
            uuid = uuid,
            request = logMessage(type = LogType.NETWORK, message = "request"),
            curl = "curl"
        )

        // Act
        extension.networkResponse(
            tag = "Tag",
            requestUuid = uuid,
            message = "response",
            state = NetworkRequestState.SENT
        )

        // Assert
        val updated = fakeGateway.storedNetworkLogMessages.single()
        assertEquals(uuid, updated.uuid)
        assertEquals(NetworkRequestState.SUCCESS, updated.state)
        assertEquals("curl", updated.curl)
        assertEquals(
            logMessage(type = LogType.NETWORK, message = "response"),
            updated.response
        )
        assertEquals(logMessage(type = LogType.NETWORK, message = "request"), updated.request)
    }

    @Test
    fun `networkResponse keeps a non SENT state as is`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedNetworkLogMessages.clear()
        val uuid = UUID.randomUUID()
        fakeGateway.networkMessageToReturn = NetworkLogMessage(
            uuid = uuid,
            request = logMessage(type = LogType.NETWORK, message = "request")
        )

        // Act
        extension.networkResponse(
            tag = "Tag",
            requestUuid = uuid,
            message = "response",
            state = NetworkRequestState.ERROR
        )

        // Assert
        assertEquals(
            NetworkRequestState.ERROR,
            fakeGateway.storedNetworkLogMessages.single().state
        )
    }

    @Test
    fun `networkResponse persists nothing when the request is unknown`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler))
        fakeGateway.storedNetworkLogMessages.clear()
        fakeGateway.networkMessageToReturn = null

        // Act
        extension.networkResponse(
            tag = "Tag",
            requestUuid = UUID.randomUUID(),
            message = "response",
            state = NetworkRequestState.SUCCESS
        )

        // Assert
        assertTrue(fakeGateway.storedNetworkLogMessages.isEmpty())
    }

    @Test
    fun `stopAfterOneError drops every message after the first failure`() = runTest {
        // Arrange
        startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler), stopAfterOneError = true)
        var callbackCalls = 0
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
    fun `a failing callback propagates the error and the extension keeps working`() = runTest {
        // Arrange
        val fakeGateway = startTestKoin()
        val extension = createExtension(UnconfinedTestDispatcher(testScheduler), stopAfterOneError = false)
        val messages = mutableListOf<LogMessage>()
        val failure = IllegalStateException("callback failed")
        var shouldFail = true
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
        // The first message was still persisted before the callback threw
        assertTrue(fakeGateway.storedLogMessages.any { it.message == "first" })
    }
}
