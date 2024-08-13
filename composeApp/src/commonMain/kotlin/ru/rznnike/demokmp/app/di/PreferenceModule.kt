package ru.rznnike.demokmp.app.di


import org.koin.dsl.module
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.preference.createDataStore

internal val preferenceModule = module {
    single { createDataStore(get()) }
    single { PreferencesManager(get()) }
}