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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.ui.theme.backgroundDark
import ru.rznnike.demokmp.app.ui.theme.backgroundLight
import ru.rznnike.demokmp.app.ui.window.LocalWindow
import ru.rznnike.demokmp.app.ui.window.WindowFocusRequester
import ru.rznnike.demokmp.app.ui.window.logger.LoggerWindow
import ru.rznnike.demokmp.app.ui.window.setMinimumSize
import ru.rznnike.demokmp.app.utils.WithWindowViewModelStoreOwner
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.global.hotkeys.HotKeysViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.icon_linux
import ru.rznnike.demokmp.generated.resources.window_title

private val WINDOW_START_WIDTH_DP = 600.dp
private val WINDOW_START_HEIGHT_DP = 600.dp

private val WINDOW_MIN_WIDTH_DP = 500.dp
private val WINDOW_MIN_HEIGHT_DP = 500.dp

@Composable
fun ApplicationScope.MainWindow(args: Array<String>) = KoinContext {
    WithWindowViewModelStoreOwner {
        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()
        val windowConfigurationViewModel = windowViewModel<WindowConfigurationViewModel>()
        val windowConfigurationUiState by windowConfigurationViewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            windowConfigurationViewModel.setCloseWindowCallback(appConfigurationViewModel::closeApplication)
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
        val hotKeysViewModel = windowViewModel<HotKeysViewModel>()
        val defaultWindowTitle = stringResource(Res.string.window_title)
        Window(
            icon = painterResource(Res.drawable.icon_linux),
            title = defaultWindowTitle,
            onCloseRequest = windowConfigurationUiState.closeWindowCallback,
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
                    hotKeysViewModel.sendEvent(keyEvent)
                    false
                }
            }
        ) {
            CompositionLocalProvider(
                LocalWindow provides window
            ) {
                setMinimumSize(
                    width = WINDOW_MIN_WIDTH_DP,
                    height = WINDOW_MIN_HEIGHT_DP
                )
                window.title = windowConfigurationUiState.windowTitle
                if (appConfigurationUiState.isLoaded) {
                    LaunchedEffect(appConfigurationUiState.language) {
                        windowConfigurationViewModel.setWindowTitle(defaultWindowTitle)
                    }

                    MainFrame()
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