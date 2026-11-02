package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.entity.DBExampleDataEntity
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the SQL contracts of [DBExampleDataDao] directly, so the repository-visible behaviour does
 * not depend on a gateway wrapper staying unchanged.
 *
 * Lives in `desktopTest` on purpose: Room's Android in-memory builder requires a `Context`, and
 * `sqlite-bundled` ships Android-only native libraries that the host JVM running Android unit tests
 * cannot load. The code under test is shared `commonMain` code, so this run covers it for every
 * target - only Room's own SQLite driver differs per platform, and that is third-party code.
 */
class DBExampleDataDaoTest {
    private val databases = mutableListOf<MainDB>()

    @AfterTest
    fun tearDown() {
        databases.forEach { it.close() }
        databases.clear()
    }

    private fun createDao(): DBExampleDataDao {
        val database = Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        databases += database
        return database.getDBExampleDataDao()
    }

    @Test
    fun `add with an existing primary key replaces the row instead of duplicating it`() = runTest {
        // Arrange
        val dao = createDao()
        dao.add(DBExampleDataEntity(id = 42, name = "first"))

        // Act
        dao.add(DBExampleDataEntity(id = 42, name = "second"))

        // Assert
        assertEquals(listOf(DBExampleDataEntity(id = 42, name = "second")), dao.getAll())
    }
}
