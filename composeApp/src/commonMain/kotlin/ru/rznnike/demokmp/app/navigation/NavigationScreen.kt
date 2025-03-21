package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher
import ru.rznnike.demokmp.app.ui.window.LocalKeyEventDispatcher

abstract class NavigationScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Transient
    var screenKeyEventCallback: ((event: KeyEvent) -> Unit)? = null
    @Transient
    private val keyEventListener = object : KeyEventDispatcher.EventListener {
        override fun onEvent(event: KeyEvent) {
            screenKeyEventCallback?.invoke(event)
        }
    }

    @Composable
    final override fun Content() {
        LocalKeyEventDispatcher.current.screenEventListener = keyEventListener
        Layout()
    }

    @Composable
    abstract fun Layout()
}