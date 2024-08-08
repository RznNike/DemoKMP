package ru.rznnike.demokmp.data.storage

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<MainDB> = Room.databaseBuilder(
    name = DB_PATH
)