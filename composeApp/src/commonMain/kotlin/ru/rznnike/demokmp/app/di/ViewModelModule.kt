package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel

internal val viewModelModule = module {
    single { AppConfigurationViewModel() }
}