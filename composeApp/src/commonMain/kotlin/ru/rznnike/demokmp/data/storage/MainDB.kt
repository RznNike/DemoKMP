package ru.rznnike.demokmp.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.rznnike.demokmp.data.storage.dao.DBExampleDataDao
import ru.rznnike.demokmp.data.storage.entity.DBExampleDataEntity

@Database(
    entities = [
        DBExampleDataEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MainDB : RoomDatabase() {
    abstract fun getDBExampleDataDao(): DBExampleDataDao
}