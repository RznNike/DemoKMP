package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.entity.LogMessageEntity
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogType
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val SESSION_ID = 1_000L
private const val OTHER_SESSION_ID = 2_000L

/**
 * Pins the SQL contracts of [LogMessageDao] directly. The gateways exercise the same queries, but
 * only indirectly: a gateway refactor could drop a boundary case without failing any test. These
 * tests are the contract and must not be "covered" through another layer.
 *
 * Lives in `desktopTest` on purpose: Room's Android in-memory builder requires a `Context`, and
 * `sqlite-bundled` ships Android-only native libraries that the host JVM running Android unit tests
 * cannot load. The code under test is shared `commonMain` code, so this run covers it for every
 * target - only Room's own SQLite driver differs per platform, and that is third-party code.
 */
class LogMessageDaoTest {
    private val databases = mutableListOf<MainDB>()

    @AfterTest
    fun tearDown() {
        databases.forEach { it.close() }
        databases.clear()
    }

    private fun createDao(): LogMessageDao {
        val database = Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        databases += database
        return database.getLogMessageDao()
    }

    private fun message(
        timestamp: Long = 0,
        sessionId: Long = SESSION_ID
    ) = LogMessageEntity(
        type = LogType.DEFAULT,
        level = LogLevel.INFO,
        timestamp = timestamp,
        tag = "tag",
        message = "message",
        sessionId = sessionId
    )

    /** Inserts [count] rows and returns their ids in ascending order. */
    private suspend fun addMessages(dao: LogMessageDao, count: Int): List<Long> {
        repeat(count) { index -> dao.add(message(timestamp = index.toLong())) }
        return dao.getAll().sortedBy { it.id }.map { it.id }
    }

    @Test
    fun `getNthId returns null when the table is empty`() = runTest {
        // Arrange
        val dao = createDao()

        // Act
        val result = dao.getNthId(offset = 1)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getNthId with offset 1 returns the newest id`() = runTest {
        // Arrange
        val dao = createDao()
        val ids = addMessages(dao, count = 3)

        // Act
        val result = dao.getNthId(offset = 1)

        // Assert
        assertEquals(ids.last(), result)
    }

    @Test
    fun `getNthId with offset 2 returns the second newest id`() = runTest {
        // Arrange
        val dao = createDao()
        val ids = addMessages(dao, count = 3)

        // Act
        val result = dao.getNthId(offset = 2)

        // Assert
        assertEquals(ids[ids.lastIndex - 1], result)
    }

    @Test
    fun `getNthId with an offset above the row count returns null`() = runTest {
        // Arrange
        val dao = createDao()
        val ids = addMessages(dao, count = 3)

        // Act
        val result = dao.getNthId(offset = ids.size + 1)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getNthId with offset 0 returns the newest id`() = runTest {
        // Arrange: the query binds `OFFSET :offset - 1`, so offset 0 becomes OFFSET -1,
        // which SQLite clamps to 0. Documents the behaviour LogGatewayImpl relies on.
        val dao = createDao()
        val ids = addMessages(dao, count = 3)

        // Act
        val result = dao.getNthId(offset = 0)

        // Assert
        assertEquals(ids.last(), result)
    }

    @Test
    fun `getSessionIds collapses duplicate sessions`() = runTest {
        // Arrange
        val dao = createDao()
        listOf(SESSION_ID, SESSION_ID, OTHER_SESSION_ID).forEach { sessionId ->
            dao.add(message(sessionId = sessionId))
        }

        // Act
        val result = dao.getSessionIds().sorted()

        // Assert
        assertEquals(listOf(SESSION_ID, OTHER_SESSION_ID), result)
    }

    @Test
    fun `getNew excludes the border id`() = runTest {
        // Arrange
        val dao = createDao()
        val ids = addMessages(dao, count = 4)

        // Act
        val result = dao.getNew(lastId = ids[1]).map { it.id }.sorted()

        // Assert
        assertEquals(ids.drop(2), result)
    }

    @Test
    fun `deleteOldById keeps the border row and removes older rows`() = runTest {
        // Arrange
        val dao = createDao()
        val ids = addMessages(dao, count = 3)

        // Act
        dao.deleteOldById(borderId = ids[1])

        // Assert
        assertEquals(ids.drop(1), dao.getAll().sortedBy { it.id }.map { it.id })
    }

    @Test
    fun `deleteOldByTimestamp keeps the border timestamp and removes older rows`() = runTest {
        // Arrange
        val dao = createDao()
        listOf(100L, 200L, 300L).forEach { timestamp -> dao.add(message(timestamp = timestamp)) }

        // Act
        dao.deleteOldByTimestamp(borderTimestamp = 200L)

        // Assert
        assertEquals(
            listOf(200L, 300L),
            dao.getAll().sortedBy { it.id }.map { it.timestamp }
        )
    }

    @Test
    fun `deleteOldBySessionId keeps the border session and removes older sessions`() = runTest {
        // Arrange: two rows share the oldest session id, both must go
        val dao = createDao()
        listOf(1L, 1L, 2L, 3L).forEach { sessionId -> dao.add(message(sessionId = sessionId)) }

        // Act
        dao.deleteOldBySessionId(borderSessionId = 2L)

        // Assert
        assertEquals(
            listOf(2L, 3L),
            dao.getAll().sortedBy { it.id }.map { it.sessionId }
        )
    }
}
