package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey

abstract class NavigationScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() = Layout()

    @Composable
    abstract fun Layout()
}