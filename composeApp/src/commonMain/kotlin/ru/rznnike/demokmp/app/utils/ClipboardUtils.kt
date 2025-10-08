package ru.rznnike.demokmp.app.utils

import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.CoroutineScope

expect fun Clipboard.setText(text: String, scope: CoroutineScope)