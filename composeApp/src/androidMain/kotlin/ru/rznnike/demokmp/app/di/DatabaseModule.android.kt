package ru.rznnike.demokmp.app.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.domain.common.DispatcherProvider

internal actual fun getDatabaseModule() = module {
    single {
        Room
            .databaseBuilder<MainDB>(
                androidApplication(),
                "abonentplus_system.db"
            )
            .fallbackToDestructiveMigration(
                dropAllTables = true
            )
            .setQueryCoroutineContext(get<DispatcherProvider>().io)
            .build()
    }

    factory { get<MainDB>().getDBExampleDataDao() }
}