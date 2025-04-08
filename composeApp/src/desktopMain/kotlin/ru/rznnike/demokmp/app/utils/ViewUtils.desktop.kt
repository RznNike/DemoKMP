package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.onClick as onClickDesktop

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun Modifier.onClick(action: () -> Unit): Modifier = onClickDesktop(onClick = action)

@Composable
fun Modifier.clearFocusOnTap(): Modifier {
    val focusManager = LocalFocusManager.current
    return pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                focusManager.clearFocus()
            }
        )
    }
}

fun Modifier.onEnterKey(action: () -> Unit): Modifier =
    onKeyEvent { keyEvent ->
        when {
            (keyEvent.key.nativeKeyCode == Key.Enter.nativeKeyCode) && (keyEvent.type == KeyEventType.KeyUp) -> {
                action()
                true
            }
            else -> false
        }
    }