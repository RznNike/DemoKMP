package ru.rznnike.demokmp.app.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.dsl.module
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider

internal actual fun getDatabaseModule() = module {
    single {
        Room
            .databaseBuilder<MainDB>(
                name = DataConstants.DB_PATH
            )
            .fallbackToDestructiveMigration(
                dropAllTables = true
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<DispatcherProvider>().io)
            .build()
    }

    factory { get<MainDB>().getLogMessageDao() }
    factory { get<MainDB>().getLogNetworkMessageDao() }
    factory { get<MainDB>().getDBExampleDataDao() }
}