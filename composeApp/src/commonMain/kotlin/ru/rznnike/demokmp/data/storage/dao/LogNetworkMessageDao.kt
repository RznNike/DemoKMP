package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.data.storage.entity.LogNetworkMessageEntity
import java.util.*

@Dao
interface LogNetworkMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: LogNetworkMessageEntity)

    @Query("SELECT * FROM LogNetworkMessageEntity WHERE uuid == :uuid")
    suspend fun get(uuid: UUID): LogNetworkMessageEntity?

    @Query("SELECT id FROM LogNetworkMessageEntity ORDER BY id DESC LIMIT 1 OFFSET :offset - 1")
    suspend fun getNthId(offset: Int): Long?

    @Query("SELECT * FROM LogNetworkMessageEntity WHERE uuid == :uuid")
    fun getAsFlow(uuid: UUID): Flow<LogNetworkMessageEntity?>

    @Query("SELECT * FROM LogNetworkMessageEntity")
    suspend fun getAll(): List<LogNetworkMessageEntity>

    @Query("SELECT * FROM LogNetworkMessageEntity WHERE id > :lastId")
    suspend fun getNew(lastId: Long): List<LogNetworkMessageEntity>

    @Query("DELETE FROM LogNetworkMessageEntity WHERE uuid == :uuid")
    suspend fun delete(uuid: UUID)

    @Query("DELETE FROM LogNetworkMessageEntity")
    suspend fun deleteAll()

    @Query("DELETE FROM LogNetworkMessageEntity WHERE request_timestamp < :borderTimestamp")
    suspend fun deleteOldByTimestamp(borderTimestamp: Long)

    @Query("DELETE FROM LogNetworkMessageEntity WHERE id < :borderId")
    suspend fun deleteOldById(borderId: Long)

    @Query("DELETE FROM LogNetworkMessageEntity WHERE sessionId < :borderSessionId")
    suspend fun deleteOldBySessionId(borderSessionId: Long)
}