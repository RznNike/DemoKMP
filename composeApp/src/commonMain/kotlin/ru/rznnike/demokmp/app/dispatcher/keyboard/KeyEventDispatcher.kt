package ru.rznnike.demokmp.app.dispatcher.keyboard

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider

class KeyEventDispatcher(
    private val coroutineScopeProvider: CoroutineScopeProvider?
) {
    var screenEventListener: EventListener? = null

    fun sendEvent(keyEvent: KeyEvent) {
        coroutineScopeProvider?.ui?.launch {
            screenEventListener?.onEvent(keyEvent)
        }
    }

    interface EventListener {
        fun onEvent(event: KeyEvent)
    }
}