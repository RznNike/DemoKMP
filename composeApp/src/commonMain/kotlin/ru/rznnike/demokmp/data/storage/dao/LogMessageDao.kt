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

    @Query("DELETE FROM LogMessageEntity WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM LogMessageEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM LogMessageEntity WHERE id == :id")
    suspend fun get(id: Long): LogMessageEntity?

    @Query("SELECT * FROM LogMessageEntity")
    fun getAll(): Flow<List<LogMessageEntity>>
}