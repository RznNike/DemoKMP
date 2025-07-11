package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.rznnike.demokmp.data.storage.entity.LogNetworkMessageEntity
import java.util.*

@Dao
interface LogNetworkMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: LogNetworkMessageEntity)

    @Query("DELETE FROM LogNetworkMessageEntity WHERE uuid == :uuid")
    suspend fun delete(uuid: UUID)

    @Query("DELETE FROM LogNetworkMessageEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM LogNetworkMessageEntity WHERE uuid == :uuid")
    suspend fun get(uuid: UUID): LogNetworkMessageEntity?

    @Query("SELECT * FROM LogNetworkMessageEntity")
    suspend fun getAll(): List<LogNetworkMessageEntity>
}