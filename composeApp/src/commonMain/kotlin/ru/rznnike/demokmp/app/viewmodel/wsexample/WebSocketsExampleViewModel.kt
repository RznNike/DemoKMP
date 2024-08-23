package ru.rznnike.demokmp.app.viewmodel.wsexample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.OpenAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase
import ru.rznnike.demokmp.domain.utils.logger

class WebSocketsExampleViewModel : BaseUiViewModel<WebSocketsExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val openAppWSSessionUseCase: OpenAppWSSessionUseCase by inject()
    private val closeAppWSSessionUseCase: CloseAppWSSessionUseCase by inject()
    private val sendAppWSMessageUseCase: SendAppWSMessageUseCase by inject()

    var messageInput by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            openAppWSSessionUseCase(
                OpenAppWSSessionUseCase.Parameters(
                    onMessage = { message ->
                        mutableUiState.update { currentState ->
                            currentState.copy(
                                messages = currentState.messages + message
                            )
                        }
                    }
                )
            )
        }
    }

    override fun onCleared() {
        coroutineScopeProvider.default.launch {
            closeAppWSSessionUseCase()
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun onMessageInput(newValue: String) {
        messageInput = newValue
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageInput.isBlank()) return@launch

            sendAppWSMessageUseCase(messageInput).process(
                {
                    messageInput = ""
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    data class UiState(
        val messages: List<String> = emptyList()
    )
}