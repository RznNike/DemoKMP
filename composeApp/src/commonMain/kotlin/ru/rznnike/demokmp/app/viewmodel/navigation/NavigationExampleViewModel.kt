package ru.rznnike.demokmp.app.viewmodel.navigation

import androidx.compose.runtime.Immutable
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

    @Immutable
    data class UiState(
        val screenNumber: Int,
        val counter: Int = 0
    )
}