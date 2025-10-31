package ru.rznnike.demokmp.app.viewmodel.app

import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher

class ActivityViewModel(
    private val restartCallback: () -> Unit
) : BaseViewModel() {
    private val eventDispatcher: EventDispatcher by inject()

    private val eventListener = object : EventDispatcher.EventListener {
        override fun onEvent(event: AppEvent) {
            when (event) {
                is AppEvent.ActivityRestartRequested -> {
                    restartCallback()
                }
                else -> Unit
            }
        }
    }

    init {
        subscribeToEvents()
    }

    override fun onCleared() {
        eventDispatcher.removeEventListener(eventListener)
    }

    private fun subscribeToEvents() {
        eventDispatcher.addEventListener(
            appEventClasses = listOf(
                AppEvent.ActivityRestartRequested::class
            ),
            listener = eventListener
        )
    }
}