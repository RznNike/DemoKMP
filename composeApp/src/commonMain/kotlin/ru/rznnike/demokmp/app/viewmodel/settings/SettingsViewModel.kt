package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun incrementCounter() {
        _uiState.update { currentState ->
            currentState.copy(
                counter = currentState.counter + 1
            )
        }
    }
}

data class SettingsUiState(
    val counter: Int = 0
)