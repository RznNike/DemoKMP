package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

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

@Composable
fun Modifier.onClick(action: () -> Unit): Modifier =
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = action
    )