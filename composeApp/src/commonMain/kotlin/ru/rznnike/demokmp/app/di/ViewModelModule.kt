package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.app.viewmodel.language.LanguageViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel

internal val viewModelModule = module {
    single { ProfileViewModel() }
    single { LanguageViewModel() }
}