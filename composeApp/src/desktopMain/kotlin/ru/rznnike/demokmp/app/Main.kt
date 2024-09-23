package ru.rznnike.demokmp.app

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import ru.rznnike.demokmp.app.di.appComponent
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher
import ru.rznnike.demokmp.app.ui.main.mainFrame
import ru.rznnike.demokmp.app.ui.theme.backgroundDark
import ru.rznnike.demokmp.app.ui.theme.backgroundLight
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_compose
import ru.rznnike.demokmp.generated.resources.window_title
import java.awt.Dimension
import java.util.*

private val WINDOW_START_WIDTH_DP = 600.dp
private val WINDOW_START_HEIGHT_DP = 600.dp

private val WINDOW_MIN_WIDTH_DP = 500.dp
private val WINDOW_MIN_HEIGHT_DP = 500.dp

fun main(args: Array<String>) = application {
    initKoin()
    startUI(args)
}

fun initKoin() {
    startKoin {
        modules(appComponent)
        printLogger()
    }
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
private fun ApplicationScope.startUI(args: Array<String>) {
    KoinContext {
        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        val keyEventDispatcher: KeyEventDispatcher = koinInject()

        appConfigurationViewModel.setArgs(args)

        val state = rememberWindowState(
            size = DpSize(
                width = WINDOW_START_WIDTH_DP,
                height = WINDOW_START_HEIGHT_DP
            ),
            position = WindowPosition(Alignment.Center)
        )
        Window(
            icon = painterResource(Res.drawable.ic_compose),
            title = stringResource(Res.string.window_title),
            onCloseRequest = {
                appConfigurationViewModel.onCloseApplication(::exitApplication)
            },
            state = state,
            onPreviewKeyEvent = { keyEvent ->
                keyEventDispatcher.sendEvent(keyEvent)
                false
            }
        ) {
            setMinimumSize(
                width = WINDOW_MIN_WIDTH_DP,
                height = WINDOW_MIN_HEIGHT_DP
            )
            if (appConfigurationUiState.isLoaded) {
                Locale.setDefault(Locale.forLanguageTag(appConfigurationUiState.language.tag))
                window.title = stringResource(Res.string.window_title)

                ProvideNavigatorLifecycleKMPSupport {
                    mainFrame()
                }
            } else { // default background while the theme has not yet loaded
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (isSystemInDarkTheme()) backgroundDark else backgroundLight
                        )
                )
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
    key(density) {
        window.minimumSize = with(density) {
            Dimension(width.toPx().toInt(), height.toPx().toInt())
        }
    }
}