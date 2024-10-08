package ru.rznnike.demokmp.app.viewmodel.wsexample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.GetAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage

class WebSocketsExampleViewModel : BaseUiViewModel<WebSocketsExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getAppWSSessionUseCase: GetAppWSSessionUseCase by inject()
    private val closeAppWSSessionUseCase: CloseAppWSSessionUseCase by inject()
    private val sendAppWSMessageUseCase: SendAppWSMessageUseCase by inject()

    var messageInput by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            getAppWSSessionUseCase().process(
                { result ->
                    var jobs: List<Job> = emptyList()

                    fun cancelJobs() = jobs.forEach { it.cancel() }

                    jobs = listOf(
                        viewModelScope.launch {
                            result.messages.cancellable().collect { message ->
                                mutableUiState.update { currentState ->
                                    currentState.copy(
                                        messages = currentState.messages + message
                                    )
                                }
                            }
                        },
                        viewModelScope.launch {
                            result.connectionState.collect { connectionState ->
                                mutableUiState.update { currentState ->
                                    currentState.copy(
                                        connectionState = connectionState
                                    )
                                }
                                when (connectionState) {
                                    WebSocketConnectionState.CLOSED -> {
                                        cancelJobs()
                                    }
                                    else -> Unit
                                }
                            }
                        }
                    )
                }, ::onError
            )
        }
    }

    override fun onCleared() {
        coroutineScopeProvider.default.launch {
            closeAppWSSessionUseCase()
        }
    }

    override fun provideDefaultUIState() = UiState()

    private suspend fun onError(error: Throwable) {
        errorHandler.proceed(error) { message ->
            notifier.sendAlert(message)
        }
    }

    fun onMessageInput(newValue: String) {
        messageInput = newValue
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageInput.isBlank()) return@launch

            val message = WebSocketMessage(
                text = messageInput,
                isIncoming = false
            )
            sendAppWSMessageUseCase(message).process(
                {
                    messageInput = ""
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            messages = currentState.messages + message
                        )
                    }
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    data class UiState(
        val messages: List<WebSocketMessage> = emptyList(),
        val connectionState: WebSocketConnectionState = WebSocketConnectionState.DISCONNECTED
    )
}