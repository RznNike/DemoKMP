package ru.rznnike.demokmp.app.viewmodel.global.configuration

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel

class WindowConfigurationViewModel : BaseUiViewModel<WindowConfigurationViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState()

    fun setWindowTitle(newValue: String) {
        viewModelScope.launch {
            mutableUiState.update { currentState ->
                currentState.copy(
                    windowTitle = newValue
                )
            }
        }
    }

    fun setCloseWindowCallback(newValue: (() -> Unit)) {
        if (mutableUiState.value.closeWindowCallback == newValue) return

        mutableUiState.update { currentState ->
            currentState.copy(
                closeWindowCallback = newValue
            )
        }
    }

    data class UiState(
        val windowTitle: String = "",
        val closeWindowCallback: (() -> Unit) = { }
    )
}