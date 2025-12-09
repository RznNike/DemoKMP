package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.rznnike.demokmp.app.model.common.HotkeyDescription
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.ui.viewmodel.global.hotkeys.HotKeysViewModel

@Serializable
abstract class DesktopNavigationScreen : NavigationScreen() {
    @Transient
    var screenKeyEventCallback: ((event: KeyEvent) -> Unit)? = null
    @Transient
    private val keyEventListener = HotKeysViewModel.EventListener { event ->
        screenKeyEventCallback?.invoke(event)
    }

    @Composable
    final override fun Content() {
        windowViewModel<HotKeysViewModel>().apply {
            setScreenEventListener(keyEventListener)
            setScreenHotkeysDescription(getHotkeysDescription())
        }
        super.Content()
    }

    @Composable
    open fun getHotkeysDescription(): List<HotkeyDescription> = emptyList()
}