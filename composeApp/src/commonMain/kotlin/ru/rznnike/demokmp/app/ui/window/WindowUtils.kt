package ru.rznnike.demokmp.app.ui.window

import androidx.compose.runtime.staticCompositionLocalOf
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher

val LocalKeyEventDispatcher = staticCompositionLocalOf { KeyEventDispatcher(null) }
val LocalWindowCloseCallback = staticCompositionLocalOf { { } }