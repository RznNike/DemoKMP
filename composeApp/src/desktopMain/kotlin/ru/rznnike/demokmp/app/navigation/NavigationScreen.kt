package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher

abstract class NavigationScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    private val keyEventListener = object : KeyEventDispatcher.EventListener {
        override fun onEvent(event: KeyEvent) {
            screenKeyEventCallback?.invoke(event)
        }
    }
    var screenKeyEventCallback: ((event: KeyEvent) -> Unit)? = null

    @Composable
    final override fun Content() {
        val keyEventDispatcher: KeyEventDispatcher = koinInject()
        keyEventDispatcher.screenEventListener = keyEventListener
        Layout()
    }

    @Composable
    abstract fun Layout()
}