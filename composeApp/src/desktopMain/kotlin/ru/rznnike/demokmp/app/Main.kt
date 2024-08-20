package ru.rznnike.demokmp.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.window_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.di.appComponent
import ru.rznnike.demokmp.app.ui.main.mainFrame
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import java.util.*

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
    KoinContext {
        val notifier: Notifier = koinInject()

        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfiguration by appConfigurationViewModel.uiState.collectAsState()

        val state = rememberWindowState(
            size = DpSize(800.dp, 800.dp),
            position = WindowPosition(Alignment.Center)
        )
        Window(
            title = stringResource(Res.string.window_title),
            onCloseRequest = ::exitApplication,
            state = state,
            onPreviewKeyEvent = { keyEvent ->
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.F) && (keyEvent.type == KeyEventType.KeyDown) -> {
                        notifier.sendMessage("Ctrl+F")
                        true
                    }
                    keyEvent.isCtrlPressed && keyEvent.isAltPressed && (keyEvent.key == Key.D) && (keyEvent.type == KeyEventType.KeyDown) -> {
                        notifier.sendMessage("Ctrl+Alt+D")
                        true
                    }
                    else -> false
                }
            }
        ) {
            if (appConfiguration.loaded) {
                Locale.setDefault(Locale.forLanguageTag(appConfiguration.language.tag))
                window.title = stringResource(Res.string.window_title)

                ProvideNavigatorLifecycleKMPSupport {
                    mainFrame()
                }
            }
        }
    }
}