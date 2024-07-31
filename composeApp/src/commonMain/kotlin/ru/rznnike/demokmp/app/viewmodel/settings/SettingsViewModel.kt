package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.preference.dataStorePreferences
import ru.rznnike.demokmp.data.utils.logT

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val preferencesManager = PreferencesManager(dataStorePreferences())

    init {
        logT("SettingsViewModel init")
        viewModelScope.launch {
            val savedCounter = preferencesManager.getTestCounter()

            _uiState.update { currentState ->
                currentState.copy(
                    counter = savedCounter
                )
            }
        }
    }

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = _uiState.value.counter + 1
            preferencesManager.setTestCounter(newCounter)

            _uiState.update { currentState ->
                currentState.copy(
                    counter = newCounter
                )
            }
        }
    }
}

data class SettingsUiState(
    val counter: Int = 0
)