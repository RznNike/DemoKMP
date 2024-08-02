package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase

internal val interactorModule = module {
    single { GetTestCounterUseCase(get(), get()) }
    single { SetTestCounterUseCase(get(), get()) }
}