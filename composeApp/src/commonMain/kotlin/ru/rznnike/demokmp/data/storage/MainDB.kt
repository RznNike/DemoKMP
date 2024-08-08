package ru.rznnike.demokmp.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.rznnike.demokmp.data.storage.dao.DBExampleDataDao
import ru.rznnike.demokmp.data.storage.entity.DBExampleDataEntity
import ru.rznnike.demokmp.domain.utils.GlobalConstants

const val DB_PATH = "${GlobalConstants.APP_DATA_DIR}/demokmp_main.db"

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

expect fun getDatabaseBuilder(): RoomDatabase.Builder<MainDB>