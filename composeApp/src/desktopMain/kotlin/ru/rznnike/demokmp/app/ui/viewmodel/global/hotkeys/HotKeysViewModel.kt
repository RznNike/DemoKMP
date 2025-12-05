package ru.rznnike.demokmp.app.ui.viewmodel.global.hotkeys

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.update
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.model.common.HotkeyDescription

class HotKeysViewModel : BaseUiViewModel<HotKeysViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState()

    fun setScreenHotkeysDescription(newValue: List<HotkeyDescription>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                screenHotkeysDescription = newValue
            )
        }
    }

    fun setCommonHotkeysDescription(newValue: List<HotkeyDescription>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                commonHotkeysDescription = newValue
            )
        }
    }

    fun setScreenEventListener(newValue: EventListener) {
        mutableUiState.update { currentState ->
            currentState.copy(
                screenEventListener = newValue
            )
        }
    }

    data class UiState(
        val screenHotkeysDescription: List<HotkeyDescription> = emptyList(),
        val commonHotkeysDescription: List<HotkeyDescription> = emptyList(),
        val screenEventListener: EventListener = EventListener {}
    )

    fun interface EventListener {
        operator fun invoke(event: KeyEvent)
    }
}