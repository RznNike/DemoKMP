package ru.rznnike.demokmp.app.viewmodel.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase

class SettingsViewModel : BaseUiViewModel<SettingsViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val getTestCounterUseCase: GetTestCounterUseCase by inject()
    private val setTestCounterUseCase: SetTestCounterUseCase by inject()

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            getTestCounterUseCase().process(
                { result ->
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            counter = result
                        )
                    }
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun onCounterInput(newValue: Int) {
        viewModelScope.launch {
            setTestCounterUseCase(newValue)

            mutableUiState.update { currentState ->
                currentState.copy(
                    counter = newValue
                )
            }
        }
    }

    data class UiState(
        val counter: Int = 0
    )
}