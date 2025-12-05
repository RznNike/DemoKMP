package ru.rznnike.demokmp.app.ui.window.logger

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.CreateNavHost
import ru.rznnike.demokmp.app.ui.screen.logger.LoggerFlow
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.ui.window.*
import ru.rznnike.demokmp.app.utils.CustomUiScale
import ru.rznnike.demokmp.app.utils.WithWindowViewModelStoreOwner
import ru.rznnike.demokmp.app.utils.clearFocusOnTap
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel
import ru.rznnike.demokmp.app.ui.viewmodel.global.hotkeys.HotKeysViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.app_name
import ru.rznnike.demokmp.generated.resources.icon_linux
import ru.rznnike.demokmp.generated.resources.logger

private val WINDOW_START_WIDTH_DP = 1024.dp
private val WINDOW_START_HEIGHT_DP = 700.dp

private val WINDOW_MIN_WIDTH_DP = 1024.dp
private val WINDOW_MIN_HEIGHT_DP = 700.dp

@Composable
fun LoggerWindow(
    focusRequester: WindowFocusRequester,
    onCloseRequest: () -> Unit
) = WithWindowViewModelStoreOwner {
    val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
    val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()
    val windowConfigurationViewModel = windowViewModel<WindowConfigurationViewModel>()
    val windowConfigurationUiState by windowConfigurationViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        windowConfigurationViewModel.setCloseWindowCallback(onCloseRequest)
    }

    val state = rememberWindowState(
        size = DpSize(
            width = WINDOW_START_WIDTH_DP,
            height = WINDOW_START_HEIGHT_DP
        ),
        position = WindowPosition(Alignment.Center),
        placement = WindowPlacement.Floating
    )
    val hotKeysViewModel = windowViewModel<HotKeysViewModel>()
    val hotKeysUiState by hotKeysViewModel.uiState.collectAsState()

    val loggerName = stringResource(Res.string.logger)
    val appName = stringResource(Res.string.app_name)
    val defaultWindowTitle = remember(appConfigurationUiState.language) {
        "$loggerName | $appName"
    }
    Window(
        icon = painterResource(Res.drawable.icon_linux),
        title = defaultWindowTitle,
        onCloseRequest = windowConfigurationUiState.closeWindowCallback,
        state = state,
        onPreviewKeyEvent = { keyEvent ->
            hotKeysUiState.screenEventListener(keyEvent)
            false
        }
    ) {
        CompositionLocalProvider(
            LocalWindow provides window
        ) {
            focusRequester.onFocusRequested = {
                window.toFront()
            }
            SetMinimumSize(
                width = WINDOW_MIN_WIDTH_DP,
                height = WINDOW_MIN_HEIGHT_DP,
                scale = appConfigurationUiState.uiScale.value
            )
            window.title = windowConfigurationUiState.windowTitle
            LaunchedEffect(appConfigurationUiState.language) {
                windowConfigurationViewModel.setWindowTitle(defaultWindowTitle)
            }

            CustomUiScale(
                appConfigurationUiState.uiScale
            ) {
                AppTheme {
                    BackgroundBox(
                        modifier = Modifier.clearFocusOnTap()
                    ) {
                        CreateNavHost(LoggerFlow())
                    }
                }
            }
        }
    }
}