package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher
import ru.rznnike.demokmp.app.ui.window.LocalKeyEventDispatcher

abstract class DesktopNavigationScreen : NavigationScreen() {
    var screenKeyEventCallback: ((event: KeyEvent) -> Unit)? = null
    private val keyEventListener = object : KeyEventDispatcher.EventListener {
        override fun onEvent(event: KeyEvent) {
            screenKeyEventCallback?.invoke(event)
        }
    }

    @Composable
    final override fun Content() {
        LocalKeyEventDispatcher.current.screenEventListener = keyEventListener
        super.Content()
    }
}