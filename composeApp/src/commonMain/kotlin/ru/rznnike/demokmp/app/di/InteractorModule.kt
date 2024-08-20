package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.domain.interactor.dbexample.*
import ru.rznnike.demokmp.domain.interactor.networkexample.GetRandomImageLinksUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.*

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
}