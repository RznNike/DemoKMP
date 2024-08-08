package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.getDatabaseBuilder

internal val databaseModule = module {
    single {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(
                dropAllTables = true
            )
            .build()
    }

    factory { get<MainDB>().getDBExampleDataDao() }
}
