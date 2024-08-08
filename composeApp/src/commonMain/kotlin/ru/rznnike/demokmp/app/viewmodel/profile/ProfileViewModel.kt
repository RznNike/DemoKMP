package ru.rznnike.demokmp.app.viewmodel.profile

import kotlinx.coroutines.flow.update
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel

class ProfileViewModel : BaseUiViewModel<ProfileViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState()

    fun onNameInput(newValue: String) {
        if (newValue != mutableUiState.value.nameInput) {
            mutableUiState.update { currentState ->
                currentState.copy(
                    nameInput = newValue
                )
            }
        }
    }

    data class UiState(
        val nameInput: String = "qwe"
    )
}