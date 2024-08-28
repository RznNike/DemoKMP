package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.domain.interactor.dbexample.*
import ru.rznnike.demokmp.domain.interactor.httpexample.GetRandomImageLinksUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.*
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.OpenAppWSUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase

internal val interactorModule = module {
    single { GetTestCounterUseCase(get(), get()) }
    single { SetTestCounterUseCase(get(), get()) }
    single { GetLanguageUseCase(get(), get()) }
    single { SetLanguageUseCase(get(), get()) }
    single { GetThemeUseCase(get(), get()) }
    single { SetThemeUseCase(get(), get()) }

    single { GetRandomImageLinksUseCase(get(), get()) }

    single { GetDBExampleDataUseCase(get(), get()) }
    single { GetDBExampleDataListUseCase(get(), get()) }
    single { AddDBExampleDataUseCase(get(), get()) }
    single { DeleteDBExampleDataUseCase(get(), get()) }
    single { DeleteAllDBExampleDataUseCase(get(), get()) }
    single { CloseDBUseCase(get(), get()) }

    single { OpenAppWSUseCase(get(), get()) }
    single { CloseAppWSUseCase(get(), get()) }
    single { SendAppWSMessageUseCase(get(), get()) }
}