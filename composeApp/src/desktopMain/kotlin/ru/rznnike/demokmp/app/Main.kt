package ru.rznnike.demokmp.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.window_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import ru.rznnike.demokmp.app.di.appComponent
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.splash.SplashFlow

fun main() = application {
    initKoin()
    startUI()
}

fun initKoin() {
    startKoin {
        modules(appComponent)
        printLogger()
    }
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
private fun ApplicationScope.startUI() {
    val state = rememberWindowState(
        size = DpSize(800.dp, 800.dp),
        position = WindowPosition(Alignment.Center)
    )
    Window(
        title = stringResource(Res.string.window_title),
        onCloseRequest = ::exitApplication,
        state = state
    ) {
        KoinContext {
            ProvideNavigatorLifecycleKMPSupport {
                createNavigator(SplashFlow())
            }
        }
    }
}