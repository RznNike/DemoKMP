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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.screen.home.HomeFlow
import ru.rznnike.demokmp.app.ui.theme.LocalCustomDrawables
import ru.rznnike.demokmp.app.viewmodel.splash.SplashViewModel

private const val ANIMATION_DURATION_MS = 1000
private const val SPLASH_DURATION_MS = 1500L

@Serializable
class SplashScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel {
            SplashViewModel { command ->
                when (command) {
                    SplashViewModel.NavigationCommand.MAIN -> navigator.newRootFlow(HomeFlow())
                }
            }
        }

        var imageVisible by remember { mutableStateOf(false) }
        val imageAlpha: Float by animateFloatAsState(
            targetValue = if (imageVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION_MS,
                easing = LinearEasing
            )
        )

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
                painter = painterResource(LocalCustomDrawables.current.ic_compose),
                contentDescription = null
            )
        }

        LaunchedEffect(Unit) {
            imageVisible = true

            delay(SPLASH_DURATION_MS)
            viewModel.setUiReady()
        }
    }
}