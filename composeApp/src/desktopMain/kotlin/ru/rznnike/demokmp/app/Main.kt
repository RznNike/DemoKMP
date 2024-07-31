package ru.rznnike.demokmp.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.window_title
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.splash.SplashFlow

@OptIn(ExperimentalVoyagerApi::class)
fun main() = application {
    val state = rememberWindowState(
        size = DpSize(800.dp, 800.dp),
        position = WindowPosition(Alignment.Center)
    )
    Window(
        title = stringResource(Res.string.window_title),
        onCloseRequest = ::exitApplication,
        state = state
    ) {
        ProvideNavigatorLifecycleKMPSupport {
            createNavigator(SplashFlow())
        }
    }
}