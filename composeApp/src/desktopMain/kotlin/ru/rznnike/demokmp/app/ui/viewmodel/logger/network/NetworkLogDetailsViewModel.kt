package ru.rznnike.demokmp.app.ui.viewmodel.logger.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageAsFlowUseCase
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString

class NetworkLogDetailsViewModel(
    private val initMessage: LogNetworkMessage
) : BaseUiViewModel<NetworkLogDetailsViewModel.UiState>() {
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getLogNetworkMessageAsFlowUseCase: GetLogNetworkMessageAsFlowUseCase by inject()

    var queryInput by mutableStateOf("")
        private set

    init {
        subscribeToUpdates()
    }

    override fun provideDefaultUIState() = UiState(
        logNetworkMessage = initMessage
    )

    private fun subscribeToUpdates() {
        viewModelScope.launch {
            getLogNetworkMessageAsFlowUseCase(initMessage.uuid).collect { newMessage ->
                newMessage?.response?.let {
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            logNetworkMessage = newMessage
                        )
                    }
                }
            }
        }
    }

    fun onQueryInput(newValue: String) {
        queryInput = newValue
        val queryMatches = if (queryInput.isNotEmpty()) {
            mutableUiState.value.logNetworkMessage.let { log ->
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

    fun openSaveLogDialog(showFileDialog: suspend (String) -> PlatformFile?) {
        coroutineScopeProvider.io.launch {
            val saveFileName = DataConstants.LOG_FILE_NAME_TEMPLATE.format(
                mutableUiState.value
                    .logNetworkMessage
                    .request
                    .timestamp
                    .toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
            )
            val file = showFileDialog(saveFileName)
            file?.writeString(getFullText())
        }
    }

    fun getFullText() = mutableUiState.value.logNetworkMessage.let { message ->
        val stringBuilder = StringBuilder()
            .appendLine(message.request.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS))
            .append(message.request.getFormattedMessage())
        message.response?.let { response ->
            stringBuilder
                .appendLine()
                .appendLine()
                .appendLine(response.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS))
                .append(response.getFormattedMessage())
        }

        stringBuilder.toString()
    }

    data class UiState(
        val logNetworkMessage: LogNetworkMessage,
        val queryMatches: Int = 0
    )
}