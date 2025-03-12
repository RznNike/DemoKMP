package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.preference.createSettings

internal val preferenceModule = module {
    single { createSettings() }
    single { PreferencesManager(get()) }
}