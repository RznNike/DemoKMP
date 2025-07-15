package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.data.storage.entity.LogMessageEntity

@Dao
interface LogMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: LogMessageEntity)

    @Query("SELECT * FROM LogMessageEntity WHERE id == :id")
    suspend fun get(id: Long): LogMessageEntity?

    @Query("SELECT id FROM LogMessageEntity ORDER BY id DESC LIMIT 1 OFFSET :offset - 1")
    suspend fun getNthId(offset: Int): Long?

    @Query("SELECT * FROM LogMessageEntity")
    fun getAll(): Flow<List<LogMessageEntity>>

    @Query("SELECT DISTINCT sessionId FROM LogMessageEntity")
    suspend fun getSessionIds(): List<Long>

    @Query("DELETE FROM LogMessageEntity WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM LogMessageEntity")
    suspend fun deleteAll()

    @Query("DELETE FROM LogMessageEntity WHERE timestamp < :borderTimestamp")
    suspend fun deleteOldByTimestamp(borderTimestamp: Long)

    @Query("DELETE FROM LogMessageEntity WHERE id < :borderId")
    suspend fun deleteOldById(borderId: Long)

    @Query("DELETE FROM LogMessageEntity WHERE sessionId < :borderSessionId")
    suspend fun deleteOldBySessionId(borderSessionId: Long)
}