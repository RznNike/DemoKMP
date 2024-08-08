package ru.rznnike.demokmp.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.rznnike.demokmp.data.storage.entity.DBExampleDataEntity

@Dao
interface DBExampleDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: DBExampleDataEntity)

    @Query("DELETE FROM DBExampleDataEntity WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM DBExampleDataEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM DBExampleDataEntity WHERE id == :id")
    suspend fun get(id: Long): DBExampleDataEntity?

    @Query("SELECT * FROM DBExampleDataEntity")
    suspend fun getAll(): List<DBExampleDataEntity>
}