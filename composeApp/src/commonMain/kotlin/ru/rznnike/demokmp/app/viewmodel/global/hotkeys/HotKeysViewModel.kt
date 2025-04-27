package ru.rznnike.demokmp.app.viewmodel.global.hotkeys

import androidx.compose.ui.input.key.KeyEvent
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel

class HotKeysViewModel : BaseViewModel() {
    private var screenEventListener = EventListener {  }

    fun sendEvent(keyEvent: KeyEvent) {
        screenEventListener(keyEvent)
    }

    fun setScreenEventListener(newValue: EventListener) {
        screenEventListener = newValue
    }

    fun interface EventListener {
        operator fun invoke(event: KeyEvent)
    }
}