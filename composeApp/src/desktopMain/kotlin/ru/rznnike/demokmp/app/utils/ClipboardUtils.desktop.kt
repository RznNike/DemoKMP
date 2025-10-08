package ru.rznnike.demokmp.app.utils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.datatransfer.StringSelection

actual fun Clipboard.setText(
    text: String,
    scope: CoroutineScope
) {
    scope.launch {
        setClipEntry(ClipEntry(StringSelection(text)))
    }
}