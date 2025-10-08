package ru.rznnike.demokmp.app.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

actual fun Clipboard.setText(
    text: String,
    scope: CoroutineScope
) {
    scope.launch {
        setClipEntry(ClipEntry(ClipData.newPlainText("", text)))
    }
}