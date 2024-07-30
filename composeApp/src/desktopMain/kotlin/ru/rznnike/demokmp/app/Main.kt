package ru.rznnike.demokmp.app

import App
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import ru.rznnike.demokmp.app.ui.splash.SplashFlow

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(800.dp, 800.dp),
        position = WindowPosition(Alignment.Center)
    )
    Window(
        title = "TestKMP",
        onCloseRequest = ::exitApplication,
        state = state
    ) {
        Navigator(SplashFlow())
    }
}