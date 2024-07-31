package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.data.preference.dataStorePreferences

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val dataStore = dataStorePreferences()
    private val testPrefKey = intPreferencesKey("TEST_COUNTER")

    init {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                val savedCounter = preferences[testPrefKey] ?: 0

                _uiState.update { currentState ->
                    currentState.copy(
                        counter = savedCounter
                    )
                }
            }
        }
    }

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = _uiState.value.counter + 1
            dataStore.edit { preferences ->
                preferences[testPrefKey] = newCounter
            }
            _uiState.update { currentState ->
                currentState.copy(
                    counter = currentState.counter + 1
                )
            }
        }
    }
}

data class SettingsUiState(
    val counter: Int = 0
)