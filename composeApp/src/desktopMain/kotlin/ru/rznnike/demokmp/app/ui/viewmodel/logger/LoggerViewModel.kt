package ru.rznnike.demokmp.app.ui.viewmodel.logger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.log.*
import ru.rznnike.demokmp.domain.log.LogEvent
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.logs_all_header
import ru.rznnike.demokmp.generated.resources.logs_network_header
import java.time.Clock

class LoggerViewModel : BaseUiViewModel<LoggerViewModel.UiState>() {
    private val clock: Clock by inject()
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getLogUseCase: GetLogUseCase by inject()
    private val getNewLogUseCase: GetNewLogUseCase by inject()
    private val getNetworkLogUseCase: GetNetworkLogUseCase by inject()
    private val getNewNetworkLogUseCase: GetNewNetworkLogUseCase by inject()
    private val clearLogUseCase: ClearLogUseCase by inject()
    private val clearNetworkLogUseCase: ClearNetworkLogUseCase by inject()
    private val saveLogToFileUseCase: SaveLogToFileUseCase by inject()

    private var log = emptyList<LogMessage>()
    private var networkLog = mutableListOf<NetworkLogMessage>()

    var filterInput by mutableStateOf("")
        private set

    init {
        subscribeToLogs()
    }

    override fun provideDefaultUIState() = UiState()

    private fun subscribeToLogs() {
        fun setLog(newValue: List<LogMessage>) {
            log = newValue
            filterLog()
        }

        fun setNetworkLog(newValue: List<NetworkLogMessage>) {
            networkLog = newValue.toMutableList()
            filterNetworkLog()
        }

        viewModelScope.launch {
            getLogUseCase().process(
                { result ->
                    setLog(result.log)
                    result.eventsFlow.collect { event ->
                        when (event) {
                            LogEvent.NewMessage -> {
                                getNewLogUseCase(log.lastOrNull()?.id ?: 0).process(
                                    { result ->
                                        setLog(log + result)
                                    }, ::onError
                                )
                            }
                            LogEvent.Cleanup -> {
                                getLogUseCase().process(
                                    { result ->
                                        setLog(result.log)
                                    }, ::onError
                                )
                            }
                            else -> Unit
                        }
                    }
                }, ::onError
            )
        }

        viewModelScope.launch {
            getNetworkLogUseCase().process(
                { result ->
                    setNetworkLog(result.log)
                    result.eventsFlow.collect { event ->
                        when (event) {
                            is LogEvent.NewNetworkMessage -> {
                                val oldMessageIndex = networkLog.indexOfFirst { it.id == event.message.id }
                                if (oldMessageIndex >= 0) {
                                    networkLog[oldMessageIndex] = event.message
                                }

                                getNewNetworkLogUseCase(networkLog.lastOrNull()?.id ?: 0).process(
                                    { result ->
                                        setNetworkLog(networkLog + result)
                                    }, ::onError
                                )
                            }
                            LogEvent.Cleanup -> {
                                getNetworkLogUseCase().process(
                                    { result ->
                                        setNetworkLog(result.log)
                                    }, ::onError
                                )
                            }
                            else -> Unit
                        }
                    }
                }, ::onError
            )
        }
    }

    private suspend fun onError(error: Throwable) {
        errorHandler.proceed(error) { message ->
            notifier.sendAlert(message)
        }
    }

    private fun filterLog() {
        val filteredLog = mutableUiState.value.run {
            log.filter { message ->
                val tagMatches = message.tag.contains(filterInput, true)
                val messageString = if (collapseNetworkMessages && (message.type == LogType.NETWORK)) {
                    message.message.lines().first()
                } else {
                    message.message
                }
                val messageMatches = messageString.contains(filterInput, true)

                (tagMatches || (messageMatches && (!filterOnlyByTag)))
                        && ((!showOnlyCurrentSession) || message.isCurrentSession)
            }
        }

        mutableUiState.update { currentState ->
            currentState.copy(
                filteredLog = filteredLog
            )
        }
    }

    private fun filterNetworkLog() {
        val filteredNetworkLog = networkLog.filter {
            val requestMatches = it.request.message.lines().first().contains(filterInput, true)
            val responseMatches = it.response?.message?.lines()?.first()?.contains(filterInput, true) == true

            (requestMatches || responseMatches) && ((!mutableUiState.value.showOnlyCurrentSession) || it.isCurrentSession)
        }

        mutableUiState.update { currentState ->
            currentState.copy(
                filteredNetworkLog = filteredNetworkLog
            )
        }
    }

    fun onFilterInput(newValue: String) {
        filterInput = newValue
        filterLog()
        filterNetworkLog()
    }

    fun onAutoscrollClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                autoscroll = !currentState.autoscroll
            )
        }
    }

    fun onShowOnlyCurrentSessionClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                showOnlyCurrentSession = !currentState.showOnlyCurrentSession
            )
        }
        filterLog()
        filterNetworkLog()
    }

    fun onCollapseNetworkMessagesClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                collapseNetworkMessages = !currentState.collapseNetworkMessages
            )
        }
        filterLog()
    }

    fun onFilterOnlyByTagClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                filterOnlyByTag = !currentState.filterOnlyByTag
            )
        }
        filterLog()
    }

    fun onTabSelected(newValue: Tab) {
        if (newValue == mutableUiState.value.selectedTab) return

        mutableUiState.update { currentState ->
            currentState.copy(
                selectedTab = newValue
            )
        }
    }

    fun deleteLog() {
        coroutineScopeProvider.io.launch {
            when (mutableUiState.value.selectedTab) {
                Tab.ALL -> {
                    clearLogUseCase()
                }
                Tab.NETWORK -> {
                    clearNetworkLogUseCase()
                }
            }
        }
    }

    fun openSaveLogDialog(showFileDialog: suspend (String) -> PlatformFile?) {
        coroutineScopeProvider.io.launch {
            val suggestedFileName = DataConstants.LOG_FILE_NAME_TEMPLATE.format(
                clock.millis().toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
            )
            showFileDialog(suggestedFileName)?.let { platformFile ->
                saveLogToFileUseCase(platformFile.file).process(
                    { }, ::onError
                )
            }
        }
    }

    data class UiState(
        val selectedTab: Tab = Tab.ALL,
        val autoscroll: Boolean = true,
        val showOnlyCurrentSession: Boolean = false,
        val collapseNetworkMessages: Boolean = false,
        val filterOnlyByTag: Boolean = false,
        val filteredLog: List<LogMessage> = emptyList(),
        val filteredNetworkLog: List<NetworkLogMessage> = emptyList()
    )

    enum class Tab(
        val nameRes: StringResource
    ) {
        ALL(Res.string.logs_all_header),
        NETWORK(Res.string.logs_network_header)
    }
}