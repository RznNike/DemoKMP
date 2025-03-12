package ru.rznnike.demokmp.app.viewmodel.home

import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher

class HomeViewModel : BaseViewModel() {
    private val eventDispatcher: EventDispatcher by inject()

    fun restartApp() {
        eventDispatcher.sendEvent(AppEvent.RestartRequested)
    }
}