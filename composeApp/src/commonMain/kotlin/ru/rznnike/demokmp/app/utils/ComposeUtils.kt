package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.addIf(condition: Boolean, callChain: @Composable Modifier.() -> Modifier): Modifier =
    if (condition) callChain() else this