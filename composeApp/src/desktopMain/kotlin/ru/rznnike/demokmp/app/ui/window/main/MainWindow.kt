package ru.rznnike.demokmp.app.ui.window.main

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.dispatcher.keyboard.KeyEventDispatcher
import ru.rznnike.demokmp.app.ui.theme.backgroundDark
import ru.rznnike.demokmp.app.ui.theme.backgroundLight
import ru.rznnike.demokmp.app.ui.window.*
import ru.rznnike.demokmp.app.ui.window.logger.LoggerWindow
import ru.rznnike.demokmp.app.utils.WithWindowViewModelStoreOwner
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.icon_linux
import ru.rznnike.demokmp.generated.resources.window_title
import java.util.*

private val WINDOW_START_WIDTH_DP = 600.dp
private val WINDOW_START_HEIGHT_DP = 600.dp

private val WINDOW_MIN_WIDTH_DP = 500.dp
private val WINDOW_MIN_HEIGHT_DP = 500.dp

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun ApplicationScope.MainWindow(args: Array<String>) = KoinContext {
    WithWindowViewModelStoreOwner {
        val appConfigurationViewModel = windowViewModel<AppConfigurationViewModel>()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        LaunchedEffect("init") {
            appConfigurationViewModel.setArgs(args)
            appConfigurationViewModel.setCloseAppCallback(::exitApplication)
        }

        var showLoggerWindow by remember { mutableStateOf(false) }
        val loggerWindowFocusRequester = remember { WindowFocusRequester() }

        val state = rememberWindowState(
            size = DpSize(
                width = WINDOW_START_WIDTH_DP,
                height = WINDOW_START_HEIGHT_DP
            ),
            position = WindowPosition(Alignment.Center),
            placement = WindowPlacement.Floating
        )
        val coroutineScopeProvider: CoroutineScopeProvider = koinInject()
        val keyEventDispatcher = remember {
            KeyEventDispatcher(
                coroutineScopeProvider = coroutineScopeProvider
            )
        }
        CompositionLocalProvider(
            LocalKeyEventDispatcher provides keyEventDispatcher
        ) {
            Window(
                icon = painterResource(Res.drawable.icon_linux),
                title = stringResource(Res.string.window_title),
                onCloseRequest = {
                    appConfigurationViewModel.closeApplication()
                },
                state = state,
                onPreviewKeyEvent = { keyEvent ->
                    if ((keyEvent.type == KeyEventType.KeyDown) && (keyEvent.key == Key.F12)) {
                        if (showLoggerWindow) {
                            loggerWindowFocusRequester.onFocusRequested()
                        } else {
                            showLoggerWindow = true
                        }
                        true
                    } else {
                        keyEventDispatcher.sendEvent(keyEvent)
                        false
                    }
                }
            ) {
                CompositionLocalProvider(
                    LocalWindow provides window,
                    LocalWindowCloseCallback provides appConfigurationViewModel::closeApplication
                ) {
                    setMinimumSize(
                        width = WINDOW_MIN_WIDTH_DP,
                        height = WINDOW_MIN_HEIGHT_DP
                    )
                    window.title = appConfigurationUiState.windowTitle
                    if (appConfigurationUiState.isLoaded) {
                        Locale.setDefault(Locale.forLanguageTag(appConfigurationUiState.language.fullTag))
                        val defaultWindowTitle = stringResource(Res.string.window_title)
                        LaunchedEffect(appConfigurationUiState.language) {
                            appConfigurationViewModel.setWindowTitle(defaultWindowTitle)
                        }

                        ProvideNavigatorLifecycleKMPSupport {
                            MainFrame()
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

        if (appConfigurationUiState.isLoaded && showLoggerWindow) {
            LoggerWindow(
                focusRequester = loggerWindowFocusRequester,
                onCloseRequest = {
                    showLoggerWindow = false
                }
            )
        } else {
            loggerWindowFocusRequester.onFocusRequested = { }
        }
    }
}