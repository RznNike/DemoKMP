package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.data.preference.PreferencesManager

class SettingsViewModel : BaseUiViewModel<SettingsUiState>() {
    private val preferencesManager: PreferencesManager by inject()

    init {
        viewModelScope.launch {
            val savedCounter = preferencesManager.getTestCounter()

            mutableUiState.update { currentState ->
                currentState.copy(
                    counter = savedCounter
                )
            }
        }
    }

    override fun provideDefaultUIState() = SettingsUiState()

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = mutableUiState.value.counter + 1
            preferencesManager.setTestCounter(newCounter)

            mutableUiState.update { currentState ->
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