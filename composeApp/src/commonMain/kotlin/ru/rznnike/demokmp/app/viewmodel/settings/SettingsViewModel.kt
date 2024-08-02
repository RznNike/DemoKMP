package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase
import ru.rznnike.demokmp.domain.utils.logger

class SettingsViewModel : BaseUiViewModel<SettingsViewModel.UiState>() {
    private val getTestCounterUseCase: GetTestCounterUseCase by inject()
    private val setTestCounterUseCase: SetTestCounterUseCase by inject()

    init {
        viewModelScope.launch {
            getTestCounterUseCase().process(
                { result ->
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            counter = result
                        )
                    }
                }, { error ->
                    logger(error)
                    // TODO
                }
            )
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun incrementCounter() {
        viewModelScope.launch {
            val newCounter = mutableUiState.value.counter + 1
            setTestCounterUseCase(newCounter)

            mutableUiState.update { currentState ->
                currentState.copy(
                    counter = newCounter
                )
            }
        }
    }

    data class UiState(
        val counter: Int = 0
    )
}