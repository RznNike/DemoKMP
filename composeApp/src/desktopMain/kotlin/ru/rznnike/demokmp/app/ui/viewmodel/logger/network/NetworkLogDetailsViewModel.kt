package ru.rznnike.demokmp.app.ui.viewmodel.logger.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageAsFlowUseCase
import ru.rznnike.demokmp.domain.interactor.log.SaveNetworkLogMessageToFileUseCase
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import java.io.File

class NetworkLogDetailsViewModel(
    private val initMessage: NetworkLogMessage
) : BaseUiViewModel<NetworkLogDetailsViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getLogNetworkMessageAsFlowUseCase: GetLogNetworkMessageAsFlowUseCase by inject()
    private val saveNetworkLogMessageToFileUseCase: SaveNetworkLogMessageToFileUseCase by inject()

    var queryInput by mutableStateOf("")
        private set

    init {
        subscribeToUpdates()
    }

    override fun provideDefaultUIState() = UiState(
        networkLogMessage = initMessage
    )

    private fun subscribeToUpdates() {
        viewModelScope.launch {
            getLogNetworkMessageAsFlowUseCase(initMessage.uuid).collect { newMessage ->
                newMessage?.response?.let {
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            networkLogMessage = newMessage
                        )
                    }
                }
            }
        }
    }

    fun onQueryInput(newValue: String) {
        queryInput = newValue
        val queryMatches = if (queryInput.isNotEmpty()) {
            mutableUiState.value.networkLogMessage.let { log ->
                val queryRegex = queryInput.toRegex(RegexOption.IGNORE_CASE)
                queryRegex.findAll(log.request.getFormattedMessage()).count() + queryRegex.findAll(log.response?.getFormattedMessage() ?: "").count()
            }
        } else 0
        mutableUiState.update { currentState ->
            currentState.copy(
                queryMatches = queryMatches
            )
        }
    }

    fun getSuggestedSaveFileName() = DataConstants.LOG_FILE_NAME_TEMPLATE.format(
        mutableUiState
            .value
            .networkLogMessage
            .request
            .timestamp
            .toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
    )

    fun saveLogToFile(file: File) {
        coroutineScopeProvider.io.launch {
            saveNetworkLogMessageToFileUseCase(
                SaveNetworkLogMessageToFileUseCase.Parameters(
                    file = file,
                    message = mutableUiState.value.networkLogMessage
                )
            ).process(
                { }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    data class UiState(
        val networkLogMessage: NetworkLogMessage,
        val queryMatches: Int = 0
    )
}