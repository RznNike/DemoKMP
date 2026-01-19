package ru.rznnike.demokmp.app.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Tooltip(
    tooltip: String,
    modifier: Modifier,
    alignment: TooltipAlignment,
    content: @Composable (() -> Unit)
) = content() // stub