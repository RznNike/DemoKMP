package ru.rznnike.demokmp.data.gateway

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogEvent
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import java.io.File
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val SESSION_1_MILLIS = 1_000_000L
private const val SESSION_2_MILLIS = 2_000_000L
private const val SESSION_3_MILLIS = 3_000_000L

@OptIn(ExperimentalCoroutinesApi::class)
class LogGatewayImplTest {
    private val databases = mutableListOf<MainDB>()
    private val tempFiles = mutableListOf<File>()

    @AfterTest
    fun tearDown() {
        databases.forEach { it.close() }
        databases.clear()
        tempFiles.forEach { it.delete() }
        tempFiles.clear()
    }

    private fun createDatabase(): MainDB =
        Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
            .also { databases += it }

    private fun createGateway(
        database: MainDB = createDatabase(),
        sessionMillis: Long = SESSION_1_MILLIS
    ): LogGateway = LogGatewayImpl(
        clock = Clock.fixed(Instant.ofEpochMilli(sessionMillis), ZoneOffset.UTC),
        logMessageDao = database.getLogMessageDao(),
        logNetworkMessageDao = database.getLogNetworkMessageDao()
    )

    private fun tempFile(): File =
        Files.createTempFile("demokmp-log-gateway", ".txt").toFile().also { tempFiles += it }

    private fun logMessage(
        type: LogType = LogType.DEFAULT,
        level: LogLevel = LogLevel.INFO,
        timestamp: Long = SESSION_1_MILLIS,
        tag: String = "Tag",
        message: String = "message"
    ) = LogMessage(
        type = type,
        level = level,
        timestamp = timestamp,
        tag = tag,
        message = message
    )

    private fun networkLogMessage(
        request: LogMessage = logMessage(),
        response: LogMessage? = null,
        curl: String = "",
        state: NetworkRequestState = NetworkRequestState.SENT
    ) = NetworkLogMessage(
        uuid = UUID.randomUUID(),
        request = request,
        response = response,
        curl = curl,
        state = state
    )

    /** Writes one message per session (tagged session0, session1, ...) and returns the tags left after cleanup. */
    private suspend fun remainingTagsAfterDeleting(
        database: MainDB,
        mode: LogsRetentionMode
    ): List<String> {
        listOf(SESSION_1_MILLIS, SESSION_2_MILLIS, SESSION_3_MILLIS)
            .forEachIndexed { index, sessionMillis ->
                createGateway(database = database, sessionMillis = sessionMillis)
                    .addLogMessageToDB(logMessage(tag = "session$index"))
            }
        createGateway(database = database, sessionMillis = SESSION_3_MILLIS).deleteOldLogs(mode)
        return createGateway(database = database, sessionMillis = SESSION_3_MILLIS)
            .getLog()
            .log
            .sortedBy { it.id }
            .map { it.tag }
    }

    @Test
    fun `addLogMessageToDB stores message as current session`() = runTest {
        // Arrange
        val gateway = createGateway()
        val message = logMessage(tag = "Tag", message = "hello")

        // Act
        gateway.addLogMessageToDB(message)

        // Assert
        val stored = gateway.getLog().log.single()
        assertTrue(stored.id > 0, "id is assigned by the database")
        assertEquals(message, stored.copy(id = message.id))
        assertTrue(stored.isCurrentSession)
    }

    @Test
    fun `addLogMessageToDB emits new message event`() = runTest {
        // Arrange
        val gateway = createGateway()
        val log = gateway.getLog()
        val event = async(UnconfinedTestDispatcher(testScheduler)) { log.eventsFlow.first() }

        // Act
        gateway.addLogMessageToDB(logMessage())

        // Assert
        assertEquals(LogEvent.NewMessage, event.await())
    }

    @Test
    fun `messages stored during another session are not current`() = runTest {
        // Arrange
        val database = createDatabase()
        createGateway(database = database, sessionMillis = SESSION_1_MILLIS)
            .addLogMessageToDB(logMessage())
        val currentSessionGateway = createGateway(
            database = database,
            sessionMillis = SESSION_2_MILLIS
        )

        // Act
        val stored = currentSessionGateway.getLog().log.single()

        // Assert
        assertFalse(stored.isCurrentSession)
    }

    @Test
    fun `addLogNetworkMessageToDB stores message as current session`() = runTest {
        // Arrange
        val gateway = createGateway()
        val message = networkLogMessage(
            request = logMessage(message = "request body"),
            response = logMessage(
                timestamp = SESSION_1_MILLIS + 5,
                message = "response body"
            ),
            curl = "curl https://example.com",
            state = NetworkRequestState.SUCCESS
        )

        // Act
        gateway.addLogNetworkMessageToDB(message)

        // Assert
        val stored = gateway.getNetworkLog().log.single()
        assertTrue(stored.id > 0, "id is assigned by the database")
        assertEquals(message, stored.copy(id = message.id))
        assertTrue(stored.isCurrentSession)
    }

    @Test
    fun `addLogNetworkMessageToDB emits new network message event`() = runTest {
        // Arrange
        val gateway = createGateway()
        val networkLog = gateway.getNetworkLog()
        val event = async(UnconfinedTestDispatcher(testScheduler)) { networkLog.eventsFlow.first() }
        val message = networkLogMessage()

        // Act
        gateway.addLogNetworkMessageToDB(message)

        // Assert (the event carries the message as passed in, before the id is assigned)
        assertEquals(LogEvent.NewNetworkMessage(message), event.await())
    }

    @Test
    fun `getLogNetworkMessage returns stored message by uuid`() = runTest {
        // Arrange
        val gateway = createGateway()
        val message = networkLogMessage(curl = "curl https://example.com")
        gateway.addLogNetworkMessageToDB(message)

        // Act
        val stored = assertNotNull(gateway.getLogNetworkMessage(message.uuid))

        // Assert
        assertEquals(message, stored.copy(id = message.id))
    }

    @Test
    fun `getLogNetworkMessage returns null for unknown uuid`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogNetworkMessageToDB(networkLogMessage())

        // Act
        val result = gateway.getLogNetworkMessage(UUID.randomUUID())

        // Assert
        assertNull(result)
    }

    @Test
    fun `getLogNetworkMessageAsFlow emits stored message and null when missing`() = runTest {
        // Arrange
        val gateway = createGateway()
        val message = networkLogMessage()

        // Act & Assert
        assertNull(gateway.getLogNetworkMessageAsFlow(UUID.randomUUID()).first())

        gateway.addLogNetworkMessageToDB(message)
        val stored = assertNotNull(gateway.getLogNetworkMessageAsFlow(message.uuid).first())
        assertEquals(message, stored.copy(id = message.id))
    }

    @Test
    fun `getNewLog returns only messages newer than the given id`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogMessageToDB(logMessage(message = "first"))
        gateway.addLogMessageToDB(logMessage(message = "second"))
        gateway.addLogMessageToDB(logMessage(message = "third"))

        // Act
        val result = gateway.getNewLog(lastId = 1)

        // Assert
        assertEquals(listOf("second", "third"), result.sortedBy { it.id }.map { it.message })
    }

    @Test
    fun `getNewNetworkLog returns only messages newer than the given id`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogNetworkMessageToDB(networkLogMessage(request = logMessage(message = "first")))
        gateway.addLogNetworkMessageToDB(networkLogMessage(request = logMessage(message = "second")))
        gateway.addLogNetworkMessageToDB(networkLogMessage(request = logMessage(message = "third")))

        // Act
        val result = gateway.getNewNetworkLog(lastId = 1)

        // Assert
        assertEquals(
            listOf("second", "third"),
            result.sortedBy { it.id }.map { it.request.message }
        )
    }

    @Test
    fun `clearLog removes all messages and emits cleanup event`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogMessageToDB(logMessage())
        val log = gateway.getLog()
        val event = async(UnconfinedTestDispatcher(testScheduler)) { log.eventsFlow.first() }

        // Act
        gateway.clearLog()

        // Assert
        assertEquals(emptyList(), gateway.getLog().log)
        assertEquals(LogEvent.Cleanup, event.await())
    }

    @Test
    fun `clearNetworkLog removes all network messages and emits cleanup event`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogNetworkMessageToDB(networkLogMessage())
        val networkLog = gateway.getNetworkLog()
        val event = async(UnconfinedTestDispatcher(testScheduler)) { networkLog.eventsFlow.first() }

        // Act
        gateway.clearNetworkLog()

        // Assert
        assertEquals(emptyList(), gateway.getNetworkLog().log)
        assertEquals(LogEvent.Cleanup, event.await())
    }

    @Test
    fun `deleteOldLogs with All mode keeps all entries and emits cleanup on both flows`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogMessageToDB(logMessage())
        gateway.addLogNetworkMessageToDB(networkLogMessage())
        val log = gateway.getLog()
        val networkLog = gateway.getNetworkLog()
        val logEvent = async(UnconfinedTestDispatcher(testScheduler)) { log.eventsFlow.first() }
        val networkEvent = async(UnconfinedTestDispatcher(testScheduler)) { networkLog.eventsFlow.first() }

        // Act
        gateway.deleteOldLogs(LogsRetentionMode.All)

        // Assert
        assertEquals(1, gateway.getLog().log.size)
        assertEquals(1, gateway.getNetworkLog().log.size)
        assertEquals(LogEvent.Cleanup, logEvent.await())
        assertEquals(LogEvent.Cleanup, networkEvent.await())
    }

    @Test
    fun `deleteOldLogs with LastNSessions keeps the newest sessions`() = runTest {
        // Arrange
        val database = createDatabase()

        // Act
        val remaining = remainingTagsAfterDeleting(
            database = database,
            mode = LogsRetentionMode.LastNSessions(sessionsCount = 2)
        )

        // Assert
        assertEquals(listOf("session1", "session2"), remaining)
    }

    @Test
    fun `deleteOldLogs with LastNSessions treats a count below one as one session`() = runTest {
        // Arrange
        val database = createDatabase()

        // Act
        val remaining = remainingTagsAfterDeleting(
            database = database,
            mode = LogsRetentionMode.LastNSessions(sessionsCount = 0)
        )

        // Assert
        assertEquals(listOf("session2"), remaining)
    }

    @Test
    fun `deleteOldLogs with LastNSessions keeps everything when fewer sessions exist`() = runTest {
        // Arrange
        val database = createDatabase()

        // Act
        val remaining = remainingTagsAfterDeleting(
            database = database,
            mode = LogsRetentionMode.LastNSessions(sessionsCount = 10)
        )

        // Assert
        assertEquals(listOf("session0", "session1", "session2"), remaining)
    }

    @Test
    fun `deleteOldLogs with LastNMessages keeps the newest messages in both tables`() = runTest {
        // Arrange
        val gateway = createGateway()
        repeat(4) { index -> gateway.addLogMessageToDB(logMessage(message = "log$index")) }
        repeat(3) { index ->
            gateway.addLogNetworkMessageToDB(
                networkLogMessage(request = logMessage(message = "network$index"))
            )
        }

        // Act
        gateway.deleteOldLogs(
            LogsRetentionMode.LastNMessages(
                messagesCount = 2,
                networkMessagesCount = 1,
                clearingPeriodMs = 60_000
            )
        )

        // Assert
        assertEquals(
            listOf("log2", "log3"),
            gateway.getLog().log.sortedBy { it.id }.map { it.message }
        )
        assertEquals(
            listOf("network2"),
            gateway.getNetworkLog().log.sortedBy { it.id }.map { it.request.message }
        )
    }

    @Test
    fun `deleteOldLogs with TimePeriod deletes entries older than the window in both tables`() =
        runTest {
            // Arrange
            val gateway = createGateway()
            gateway.addLogMessageToDB(
                logMessage(timestamp = SESSION_1_MILLIS - 5_000, message = "old log")
            )
            gateway.addLogMessageToDB(
                logMessage(timestamp = SESSION_1_MILLIS - 1_000, message = "recent log")
            )
            gateway.addLogNetworkMessageToDB(
                networkLogMessage(
                    request = logMessage(
                        timestamp = SESSION_1_MILLIS - 5_000,
                        message = "old network"
                    )
                )
            )
            gateway.addLogNetworkMessageToDB(
                networkLogMessage(
                    request = logMessage(
                        timestamp = SESSION_1_MILLIS - 1_000,
                        message = "recent network"
                    )
                )
            )

            // Act
            gateway.deleteOldLogs(
                LogsRetentionMode.TimePeriod(logsRetentionMs = 2_000, clearingPeriodMs = 60_000)
            )

            // Assert (border is SESSION_1_MILLIS - 2000, so only "recent" entries survive)
            assertEquals(listOf("recent log"), gateway.getLog().log.map { it.message })
            assertEquals(
                listOf("recent network"),
                gateway.getNetworkLog().log.map { it.request.message }
            )
        }

    @Test
    fun `saveLogToFile writes formatted messages except session start`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.addLogMessageToDB(logMessage(message = "hello"))
        gateway.addLogMessageToDB(
            logMessage(type = LogType.SESSION_START, message = "session start")
        )
        val file = tempFile()

        // Act
        gateway.saveLogToFile(file)

        // Assert
        val expected = SESSION_1_MILLIS.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS) +
            " | I | Tag | hello\n"
        assertEquals(expected, file.readText())
    }

    @Test
    fun `saveNetworkLogMessageToFile writes full message text`() = runTest {
        // Arrange
        val gateway = createGateway()
        val message = networkLogMessage(
            request = logMessage(timestamp = SESSION_1_MILLIS, message = "request body"),
            response = logMessage(
                timestamp = SESSION_1_MILLIS + 1_000,
                message = "response body"
            )
        )
        val file = tempFile()

        // Act
        gateway.saveNetworkLogMessageToFile(file, message)

        // Assert
        val content = file.readText()
        assertEquals(message.getFullText(), content)
        assertTrue(content.contains("request body"))
        assertTrue(content.contains("response body"))
    }
}
