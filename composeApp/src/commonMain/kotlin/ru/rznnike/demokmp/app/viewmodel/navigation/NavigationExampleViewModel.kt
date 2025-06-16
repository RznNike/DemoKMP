package ru.rznnike.demokmp.app.viewmodel.navigation

import kotlinx.coroutines.flow.update
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel

class NavigationExampleViewModel(
    private val screenNumber: Int
) : BaseUiViewModel<NavigationExampleViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState(
        screenNumber = screenNumber
    )

    fun increaseCounter() {
        mutableUiState.update { currentState ->
            currentState.copy(
                counter = currentState.counter + 1
            )
        }
    }

    data class UiState(
        val screenNumber: Int,
        val counter: Int = 0
    )
}