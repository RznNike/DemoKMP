package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
abstract class NavigationScreen {
    @Composable
    open fun Content() = Layout()

    @Composable
    abstract fun Layout()
}