package ru.rznnike.demokmp.app.ui.viewmodel.comobjectexample

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
import ru.rznnike.demokmp.domain.interactor.comobjectexample.GetPCDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.MinimizeAllWindowsUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.OpenFolderOrFileUseCase
import kotlin.getValue

class ComObjectExampleViewModel : BaseUiViewModel<ComObjectExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val getPCDataUseCase: GetPCDataUseCase by inject()
    private val minimizeAllWindowsUseCase: MinimizeAllWindowsUseCase by inject()
    private val openFolderOrFileUseCase: OpenFolderOrFileUseCase by inject()

    var pathInput by mutableStateOf("")
        private set

    init {
        loadData()
    }

    override fun provideDefaultUIState() = UiState()

    private fun loadData() {
        viewModelScope.launch {
            getPCDataUseCase().process(
                {
                    updatePCData(it)
                }
            )
        }
    }

    fun onPathInput(newValue: String) {
        pathInput = newValue
    }

    private fun updatePCData(newValue: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                pcData = newValue
            )
        }
    }

    fun openPath() {
        viewModelScope.launch {
            if (pathInput.isBlank()) return@launch

            openFolderOrFileUseCase(pathInput).process(
                {}, ::onError
            )
        }
    }

    fun minimizeAllWindows() {
        viewModelScope.launch {
            minimizeAllWindowsUseCase().process(
                {}, ::onError
            )
        }
    }

    private suspend fun onError(error: Exception) {
        errorHandler.proceed(error) { message ->
            notifier.sendAlert(message)
        }
    }

    data class UiState(
        val pcData: String = ""
    )
}