package ru.rznnike.demokmp.app.ui.viewmodel.logger.network

import androidx.compose.runtime.Immutable
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
import ru.rznnike.demokmp.domain.interactor.log.SaveTextToFileUseCase
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import java.io.File
import java.util.regex.Pattern

class NetworkLogDetailsViewModel(
    private val initMessage: NetworkLogMessage
) : BaseUiViewModel<NetworkLogDetailsViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getLogNetworkMessageAsFlowUseCase: GetLogNetworkMessageAsFlowUseCase by inject()
    private val saveTextToFileUseCase: SaveTextToFileUseCase by inject()

    var queryInput by mutableStateOf("")
        private set

    init {
        subscribeToUpdates()
    }

    override fun provideDefaultUIState() = UiState(
        networkLogMessage = initMessage,
        requestFormattedText = getFormattedText(initMessage.request) ?: "",
        responseFormattedText = getFormattedText(initMessage.response)
    )

    private fun subscribeToUpdates() {
        viewModelScope.launch {
            getLogNetworkMessageAsFlowUseCase(initMessage.uuid).collect { newMessage ->
                newMessage?.response?.let {
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            networkLogMessage = newMessage,
                            requestFormattedText = getFormattedText(newMessage.request) ?: "",
                            responseFormattedText = getFormattedText(newMessage.response)
                        )
                    }
                    onQueryInput(queryInput)
                }
            }
        }
    }

    private fun getFormattedText(message: LogMessage?) = message?.let {
        "%s\n%s".format(
            message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
            message.getFormattedMessage()
        )
    }

    fun onQueryInput(newValue: String) {
        queryInput = newValue
        val queryMatches = if (queryInput.isNotEmpty()) {
            val pattern = queryInput.toPattern(Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.LITERAL)
            pattern.matcher(uiState.value.requestFormattedText).results().count() +
                    pattern.matcher(uiState.value.responseFormattedText ?: "").results().count()
        } else 0
        mutableUiState.update { currentState ->
            currentState.copy(
                queryMatches = queryMatches
            )
        }
    }

    fun getSuggestedSaveFileName() = DataConstants.LOG_FILE_NAME_TEMPLATE.format(
        uiState
            .value
            .networkLogMessage
            .request
            .timestamp
            .toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
    )

    fun saveLogToFile(file: File) {
        coroutineScopeProvider.io.launch {
            saveTextToFileUseCase(
                SaveTextToFileUseCase.Parameters(
                    file = file,
                    text = uiState.value.networkLogMessage.getFullText()
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

    @Immutable
    data class UiState(
        val networkLogMessage: NetworkLogMessage,
        val requestFormattedText: String,
        val responseFormattedText: String?,
        val queryMatches: Long = 0
    )
}