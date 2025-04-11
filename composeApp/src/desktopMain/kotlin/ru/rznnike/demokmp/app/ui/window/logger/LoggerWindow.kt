package ru.rznnike.demokmp.app.ui.window.logger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.screen.logger.LoggerFlow
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.ui.window.*
import ru.rznnike.demokmp.app.utils.WithWindowViewModelStoreOwner
import ru.rznnike.demokmp.app.utils.clearFocusOnTap
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.hotkeys.HotKeysViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.icon_linux
import ru.rznnike.demokmp.generated.resources.logger
import ru.rznnike.demokmp.generated.resources.window_title

private val WINDOW_START_WIDTH_DP = 1024.dp
private val WINDOW_START_HEIGHT_DP = 700.dp

private val WINDOW_MIN_WIDTH_DP = 1024.dp
private val WINDOW_MIN_HEIGHT_DP = 700.dp

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun LoggerWindow(
    focusRequester: WindowFocusRequester,
    onCloseRequest: () -> Unit
) = KoinContext {
    WithWindowViewModelStoreOwner {
        val state = rememberWindowState(
            size = DpSize(
                width = WINDOW_START_WIDTH_DP,
                height = WINDOW_START_HEIGHT_DP
            ),
            position = WindowPosition(Alignment.Center),
            placement = WindowPlacement.Floating
        )
        val hotKeysViewModel = windowViewModel<HotKeysViewModel>()
        Window(
            icon = painterResource(Res.drawable.icon_linux),
            title = "%s | %s".format(
                stringResource(Res.string.logger),
                stringResource(Res.string.window_title)
            ),
            onCloseRequest = { onCloseRequest() },
            state = state,
            onPreviewKeyEvent = { keyEvent ->
                hotKeysViewModel.sendEvent(keyEvent)
                false
            }
        ) {
            CompositionLocalProvider(
                LocalWindow provides window,
                LocalWindowCloseCallback provides onCloseRequest
            ) {
                focusRequester.onFocusRequested = {
                    window.toFront()
                }
                setMinimumSize(
                    width = WINDOW_MIN_WIDTH_DP,
                    height = WINDOW_MIN_HEIGHT_DP
                )

                ProvideNavigatorLifecycleKMPSupport {
                    AppTheme {
                        BackgroundBox(
                            modifier = Modifier.clearFocusOnTap()
                        ) {
                            createNavigator(LoggerFlow())
                        }
                    }
                }
            }
        }
    }
}