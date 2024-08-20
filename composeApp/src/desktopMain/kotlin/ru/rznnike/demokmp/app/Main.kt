package ru.rznnike.demokmp.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
import java.awt.Dimension
import java.util.*

private val WINDOW_START_WIDTH_DP = 800.dp
private val WINDOW_START_HEIGHT_DP = 800.dp

private val WINDOW_MIN_WIDTH_DP = 300.dp
private val WINDOW_MIN_HEIGHT_DP = 300.dp

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
            size = DpSize(
                width = WINDOW_START_WIDTH_DP,
                height = WINDOW_START_HEIGHT_DP
            ),
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
            setMinimumSize(
                width = WINDOW_MIN_WIDTH_DP,
                height = WINDOW_MIN_HEIGHT_DP
            )
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

@Composable
fun FrameWindowScope.setMinimumSize(
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
) {
    val density = LocalDensity.current
    LaunchedEffect(density) {
        window.minimumSize = with(density) {
            Dimension(width.toPx().toInt(), height.toPx().toInt())
        }
    }
}