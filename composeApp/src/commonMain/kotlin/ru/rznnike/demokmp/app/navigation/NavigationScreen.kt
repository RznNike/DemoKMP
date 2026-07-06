package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
abstract class NavigationScreen : NavKey {
    @Composable
    open fun Content() = Layout()

    @Composable
    abstract fun Layout()
}