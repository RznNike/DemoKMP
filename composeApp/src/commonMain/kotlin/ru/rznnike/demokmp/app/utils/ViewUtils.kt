package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Modifier.onClick(action: () -> Unit): Modifier