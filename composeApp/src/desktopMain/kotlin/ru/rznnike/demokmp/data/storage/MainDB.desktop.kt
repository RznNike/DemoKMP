package ru.rznnike.demokmp.data.storage

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.rznnike.demokmp.data.utils.DataConstants

actual fun getDatabaseBuilder(): RoomDatabase.Builder<MainDB> = Room.databaseBuilder(
    name = DataConstants.DB_PATH
)