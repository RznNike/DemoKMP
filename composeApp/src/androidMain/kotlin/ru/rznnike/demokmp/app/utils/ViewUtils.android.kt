package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.onClick(
    onDoubleClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onClick: () -> Unit
): Modifier = combinedClickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onDoubleClick = onDoubleClick,
    onLongClick = onLongClick,
    onClick = onClick
)

@Composable
fun Modifier.statusBarsAndCutoutPadding(): Modifier = statusBarsPadding().displayCutoutPadding()

@Composable
fun Modifier.navigationBarsSidesPadding(): Modifier = padding(
    WindowInsets.navigationBars
        .only(WindowInsetsSides.Horizontal)
        .asPaddingValues()
)