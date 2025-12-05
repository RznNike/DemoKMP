package ru.rznnike.demokmp.app.viewmodel.splash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.domain.interactor.app.CheckIfAppIsAlreadyRunningUseCase

class SplashViewModel(
    private val navigationCallback: (NavigationCommand) -> Unit
) : BaseViewModel() {
    private val eventDispatcher: EventDispatcher by inject()
    private val checkIfAppIsAlreadyRunningUseCase: CheckIfAppIsAlreadyRunningUseCase by inject()

    private var isUiReady = false
    private var isDataReady = false

    private var showMultiLaunchDialog: () -> Unit = { }

    init {
        checkIfAppIsAlreadyRunning()
        eventDispatcher.sendEvent(
            AppEvent.BottomStatusBarRequested(
                show = false
            )
        )
    }

    fun setDialogCallbacks(
        showMultiLaunchDialog: () -> Unit
    ) {
        this.showMultiLaunchDialog = showMultiLaunchDialog
    }

    private fun checkIfAppIsAlreadyRunning() {
        viewModelScope.launch {
            val isAlreadyRunning = checkIfAppIsAlreadyRunningUseCase().data
            if (isAlreadyRunning == false) {
                isDataReady = true
                processScreenState()
            } else {
                showMultiLaunchDialog()
            }
        }
    }

    fun setUiReady() {
        isUiReady = true
        processScreenState()
    }

    private fun processScreenState() {
        when {
            !(isDataReady && isUiReady) -> Unit
            else -> navigationCallback(NavigationCommand.MAIN)
        }
    }

    enum class NavigationCommand {
        MAIN
    }
}