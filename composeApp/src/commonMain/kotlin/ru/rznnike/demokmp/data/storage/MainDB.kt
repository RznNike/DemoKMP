package ru.rznnike.demokmp.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.rznnike.demokmp.data.storage.dao.DBExampleDataDao
import ru.rznnike.demokmp.data.storage.dao.LogMessageDao
import ru.rznnike.demokmp.data.storage.dao.LogNetworkMessageDao
import ru.rznnike.demokmp.data.storage.entity.DBExampleDataEntity
import ru.rznnike.demokmp.data.storage.entity.LogMessageEntity
import ru.rznnike.demokmp.data.storage.entity.LogNetworkMessageEntity

@Database(
    entities = [
        LogMessageEntity::class,
        LogNetworkMessageEntity::class,
        DBExampleDataEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MainDB : RoomDatabase() {
    abstract fun getDBExampleDataDao(): DBExampleDataDao

    abstract fun getLogMessageDao(): LogMessageDao

    abstract fun getLogNetworkMessageDao(): LogNetworkMessageDao
}