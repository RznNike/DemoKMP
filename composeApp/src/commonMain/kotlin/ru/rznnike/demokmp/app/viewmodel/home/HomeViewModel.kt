package ru.rznnike.demokmp.app.viewmodel.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.utils.openLink
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.github_repository_link

class HomeViewModel : BaseViewModel() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val eventDispatcher: EventDispatcher by inject()

    fun restartApp() {
        eventDispatcher.sendEvent(AppEvent.RestartRequested)
    }

    fun openSourceCodeLink() {
        viewModelScope.launch {
            try {
                openLink(getString(Res.string.github_repository_link))
            } catch (error: Exception) {
                errorHandler.proceed(error) { message ->
                    notifier.sendAlert(message)
                }
            }
        }
    }
}