package ru.rznnike.demokmp.app.ui.viewmodel.logger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.sink
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.log.ClearLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.ClearNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNetworkLogUseCase
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.logs_all_header
import ru.rznnike.demokmp.generated.resources.logs_network_header
import java.time.Clock

class LoggerViewModel : BaseUiViewModel<LoggerViewModel.UiState>() {
    private val clock: Clock by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getLogUseCase: GetLogUseCase by inject()
    private val getNetworkLogUseCase: GetNetworkLogUseCase by inject()
    private val clearLogUseCase: ClearLogUseCase by inject()
    private val clearNetworkLogUseCase: ClearNetworkLogUseCase by inject()

    private var log = emptyList<LogMessage>()
    private var networkLog = emptyList<LogNetworkMessage>()

    var filterInput by mutableStateOf("")
        private set

    init {
        subscribeToLogs()
    }

    override fun provideDefaultUIState() = UiState()

    private fun subscribeToLogs() {
        viewModelScope.launch {
            getLogUseCase().collect {
                log = it
                filterLog()
            }
        }
        viewModelScope.launch {
            getNetworkLogUseCase().collect {
                networkLog = it
                filterNetworkLog()
            }
        }
    }

    private fun filterLog() {
        val filteredLog = log.filter { message ->
            val tagMatches = message.tag.contains(filterInput, true)
            val messageString = if (mutableUiState.value.collapseNetworkMessages && (message.type == LogType.NETWORK)) {
                message.message.lines().first()
            } else {
                message.message
            }
            val messageMatches = messageString.contains(filterInput, true)

            tagMatches || (messageMatches && (!mutableUiState.value.filterOnlyByTag))
        }

        mutableUiState.update { currentState ->
            currentState.copy(
                filteredLog = filteredLog
            )
        }
    }

    private fun filterNetworkLog() {
        val filteredNetworkLog = networkLog.filter {
            it.request.message.lines().first().contains(filterInput, true)
                    || (it.response?.message?.lines()?.first()?.contains(filterInput, true) == true)
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

    fun onFilterOnlyByTagClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                filterOnlyByTag = !currentState.filterOnlyByTag
            )
        }
        filterLog()
    }

    fun onAutoscrollClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                autoscroll = !currentState.autoscroll
            )
        }
    }

    fun onCollapseNetworkMessagesClick() {
        mutableUiState.update { currentState ->
            currentState.copy(
                collapseNetworkMessages = !currentState.collapseNetworkMessages
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
            val saveFileName = DataConstants.LOG_FILE_NAME_TEMPLATE.format(
                clock.millis().toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
            )
            val file = showFileDialog(saveFileName)
            file?.sink()?.buffered()?.use { writer ->
                log.forEach { message ->
                    val text = "%s%s | %s".format(
                        message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
                        if (message.tag.isNotBlank()) " | ${message.tag}" else "",
                        message.getFormattedMessage()
                    )
                    writer.writeText(text)
                    writer.writeText("\n")
                }
            }
        }
    }

    data class UiState(
        val selectedTab: Tab = Tab.ALL,
        val autoscroll: Boolean = true,
        val filterOnlyByTag: Boolean = false,
        val collapseNetworkMessages: Boolean = false,
        val filteredLog: List<LogMessage> = emptyList(),
        val filteredNetworkLog: List<LogNetworkMessage> = emptyList()
    )

    enum class Tab(
        val nameRes: StringResource
    ) {
        ALL(Res.string.logs_all_header),
        NETWORK(Res.string.logs_network_header)
    }
}