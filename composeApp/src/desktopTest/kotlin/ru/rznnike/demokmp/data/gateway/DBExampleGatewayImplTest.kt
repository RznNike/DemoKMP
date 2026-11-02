package ru.rznnike.demokmp.data.gateway

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Lives in `desktopTest` on purpose: Room's Android in-memory builder requires a `Context`, and
 * `sqlite-bundled` ships Android-only native libraries that the host JVM running Android unit tests
 * cannot load. The code under test is shared `commonMain` code, so this run covers it for every
 * target - only Room's own SQLite driver differs per platform, and that is third-party code.
 */
class DBExampleGatewayImplTest {
    private fun createGateway(): DBExampleGateway {
        val mainDB = Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        return DBExampleGatewayImpl(
            dbExampleDataDao = mainDB.getDBExampleDataDao(),
            mainDB = mainDB
        )
    }

    @Test
    fun `add and get returns stored data`() = runTest {
        // Arrange
        val gateway = createGateway()
        val data = DBExampleData(id = 42, name = "test name")

        // Act
        gateway.add(data)

        // Assert
        assertEquals(data, gateway.get(id = 42))
    }

    @Test
    fun `get returns null for missing id`() = runTest {
        // Arrange
        val gateway = createGateway()

        // Act
        val result = gateway.get(id = 1)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getAll returns all stored data`() = runTest {
        // Arrange
        val gateway = createGateway()
        val items = listOf(
            DBExampleData(id = 1, name = "first"),
            DBExampleData(id = 2, name = "second")
        )
        items.forEach { gateway.add(it) }

        // Act
        val result = gateway.getAll()

        // Assert (sorted by id: SELECT without ORDER BY has no guaranteed order)
        assertContentEquals(items, result.sortedBy { it.id })
    }

    @Test
    fun `add with existing id replaces the record`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.add(DBExampleData(id = 1, name = "old"))

        // Act
        gateway.add(DBExampleData(id = 1, name = "new"))

        // Assert
        assertEquals(DBExampleData(id = 1, name = "new"), gateway.get(id = 1))
    }

    @Test
    fun `delete removes only the matching record`() = runTest {
        // Arrange
        val gateway = createGateway()
        val first = DBExampleData(id = 1, name = "first")
        val second = DBExampleData(id = 2, name = "second")
        gateway.add(first)
        gateway.add(second)

        // Act
        gateway.delete(first)

        // Assert
        assertEquals(listOf(second), gateway.getAll())
    }

    @Test
    fun `deleteAll removes all records`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.add(DBExampleData(id = 1, name = "first"))
        gateway.add(DBExampleData(id = 2, name = "second"))

        // Act
        gateway.deleteAll()

        // Assert
        assertEquals(emptyList(), gateway.getAll())
    }

    @Test
    fun `closeDB closes the database`() = runTest {
        // Arrange
        val gateway = createGateway()
        gateway.add(DBExampleData(id = 1, name = "first"))

        // Act
        gateway.closeDB()

        // Assert
        assertFailsWith<IllegalStateException> { gateway.getAll() }
    }
}
