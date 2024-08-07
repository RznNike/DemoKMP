package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.domain.interactor.networkexample.GetRandomImageLinksUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase

internal val interactorModule = module {
    single { GetTestCounterUseCase(get(), get()) }
    single { SetTestCounterUseCase(get(), get()) }
    single { GetLanguageUseCase(get(), get()) }
    single { SetLanguageUseCase(get(), get()) }

    single { GetRandomImageLinksUseCase(get(), get()) }
}