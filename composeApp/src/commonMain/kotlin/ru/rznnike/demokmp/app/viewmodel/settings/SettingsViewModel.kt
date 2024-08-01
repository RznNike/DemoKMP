package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.data.preference.PreferencesManager

class SettingsViewModel : BaseUiViewModel<SettingsViewModel.UiState>() {
    private val preferencesManager: PreferencesManager by inject()

    init {
        viewModelScope.launch {
            mutableUiState.update { currentState ->
                currentState.copy(
                    counter = preferencesManager.testCounter.get(),
                    counter2 = preferencesManager.testCounter2.get()
                )
            }
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = mutableUiState.value.counter + 1
            preferencesManager.testCounter.set(newCounter)

            mutableUiState.update { currentState ->
                currentState.copy(
                    counter = newCounter
                )
            }
        }
    }

    fun incrementCounter2() {
        viewModelScope.launch {
            val newCounter = mutableUiState.value.counter2 + 1
            preferencesManager.testCounter2.set(newCounter)

            mutableUiState.update { currentState ->
                currentState.copy(
                    counter2 = newCounter
                )
            }
        }
    }

    data class UiState(
        val counter: Int = 0,
        val counter2: Int = 0
    )
}