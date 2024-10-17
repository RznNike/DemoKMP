package ru.rznnike.demokmp.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.dsl.module
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import java.time.Clock

internal val appModule = module {
    single { Clock.systemUTC() }
    single { ErrorHandler() }
    single { Notifier(get()) }
    single { EventDispatcher(get()) }
    single { KeyEventDispatcher(get()) }

    single<CoroutineScopeProvider> {
        object : CoroutineScopeProvider {
            override val ui = MainScope()
            override val default = CoroutineScope(Dispatchers.Default)
            override val io = CoroutineScope(Dispatchers.IO)
            override val unconfined = CoroutineScope(Dispatchers.Unconfined)
        }
    }

    single<DispatcherProvider> {
        object : DispatcherProvider {
            override val ui = Dispatchers.Main
            override val default = Dispatchers.Default
            override val io = Dispatchers.IO
            override val unconfined = Dispatchers.Unconfined
        }
    }
}