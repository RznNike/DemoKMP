package ru.rznnike.demokmp.app.viewmodel.profile

import kotlinx.coroutines.flow.update
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel

class ProfileViewModel : BaseUiViewModel<ProfileViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState()

    fun changeName(newValue: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                name = newValue
            )
        }
    }

    data class UiState(
        val name: String = "qwe"
    )
}