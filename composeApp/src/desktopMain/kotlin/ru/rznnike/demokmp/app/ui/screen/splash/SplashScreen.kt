package ru.rznnike.demokmp.app.ui.screen.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.home.HomeFlow
import ru.rznnike.demokmp.app.ui.theme.localCustomDrawables
import ru.rznnike.demokmp.app.viewmodel.splash.SplashViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.close
import ru.rznnike.demokmp.generated.resources.error
import ru.rznnike.demokmp.generated.resources.error_app_multi_launch

private const val ANIMATION_DURATION_MS = 1000
private const val SPLASH_DURATION_MS = 1500L

class SplashScreen : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val showMultiLaunchDialog = remember { mutableStateOf(false) }

        val viewModel = viewModel {
            SplashViewModel { command ->
                when (command) {
                    SplashViewModel.NavigationCommand.MAIN -> navigator.newRootFlow(HomeFlow())
                }
            }
        }
        viewModel.setDialogCallbacks(
            showMultiLaunchDialog = {
                showMultiLaunchDialog.value = true
            }
        )

        var imageVisible by remember { mutableStateOf(false) }
        val imageAlpha: Float by animateFloatAsState(
            targetValue = if (imageVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION_MS,
                easing = LinearEasing
            )
        )

        @Composable
        fun AppUpdateDialog() {
            if (showMultiLaunchDialog.value) {
                CommonAlertDialog(
                    type = AlertDialogType.HORIZONTAL,
                    header = stringResource(Res.string.error),
                    message = stringResource(Res.string.error_app_multi_launch),
                    cancellable = false,
                    actions = listOf(
                        AlertDialogAction(stringResource(Res.string.close)) {
                            navigator.closeFlow()
                        }
                    )
                )
            }
        }

        // Main layout
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(300.dp),
                alpha = imageAlpha,
                painter = painterResource(localCustomDrawables.current.ic_compose),
                contentDescription = null
            )
        }
        AppUpdateDialog()

        LaunchedEffect("init") {
            imageVisible = true

            delay(SPLASH_DURATION_MS)
            viewModel.setUiReady()
        }
    }
}