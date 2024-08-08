package ru.rznnike.demokmp.app.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.dsl.module
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.getDatabaseBuilder
import ru.rznnike.demokmp.domain.common.DispatcherProvider

internal val databaseModule = module {
    single {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(
                dropAllTables = true
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<DispatcherProvider>().io)
            .build()
    }

    factory { get<MainDB>().getDBExampleDataDao() }
}
