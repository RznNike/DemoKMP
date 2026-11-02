package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.entity.LogNetworkMessageEntity
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val SESSION_ID = 1_000L

/**
 * Pins the SQL contracts of [LogNetworkMessageDao] directly, in particular that the `uuid` field
 * round-trips through its BLOB column (built-in Room UUID converter) instead of only through the
 * entity mapper.
 *
 * Lives in `desktopTest` on purpose: Room's Android in-memory builder requires a `Context`, and
 * `sqlite-bundled` ships Android-only native libraries that the host JVM running Android unit tests
 * cannot load. The code under test is shared `commonMain` code, so this run covers it for every
 * target - only Room's own SQLite driver differs per platform, and that is third-party code.
 */
class LogNetworkMessageDaoTest {
    private val databases = mutableListOf<MainDB>()

    @AfterTest
    fun tearDown() {
        databases.forEach { it.close() }
        databases.clear()
    }

    private fun createDao(): LogNetworkMessageDao {
        val database = Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        databases += database
        return database.getLogNetworkMessageDao()
    }

    private fun networkMessage(
        uuid: UUID = UUID.randomUUID(),
        sessionId: Long = SESSION_ID
    ) = LogNetworkMessageEntity(
        uuid = uuid,
        request = LogMessage(
            type = LogType.NETWORK,
            level = LogLevel.INFO,
            timestamp = 0,
            tag = "tag",
            message = "request body"
        ),
        curl = "curl https://example.com",
        state = NetworkRequestState.SENT,
        sessionId = sessionId
    )

    @Test
    fun `get returns the stored row by uuid`() = runTest {
        // Arrange
        val dao = createDao()
        val message = networkMessage()
        dao.add(message)

        // Act
        val result = assertNotNull(dao.get(message.uuid))

        // Assert
        assertEquals(message, result.copy(id = message.id))
    }

    @Test
    fun `get returns null for an unknown uuid`() = runTest {
        // Arrange
        val dao = createDao()
        dao.add(networkMessage())

        // Act
        val result = dao.get(UUID.randomUUID())

        // Assert
        assertNull(result)
    }

    @Test
    fun `getAsFlow emits the stored row by uuid`() = runTest {
        // Arrange
        val dao = createDao()
        val message = networkMessage()
        dao.add(message)

        // Act
        val result = assertNotNull(dao.getAsFlow(message.uuid).first())

        // Assert
        assertEquals(message, result.copy(id = message.id))
    }

    @Test
    fun `getAsFlow emits null for an unknown uuid`() = runTest {
        // Arrange
        val dao = createDao()
        dao.add(networkMessage())

        // Act
        val result = dao.getAsFlow(UUID.randomUUID()).first()

        // Assert
        assertNull(result)
    }
}
