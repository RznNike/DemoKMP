package ru.rznnike.demokmp.app.ui.screen.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.ic_compose
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.screen.home.HomeFlow

private const val ANIMATION_DURATION_MS = 1000
private const val SPLASH_DURATION_MS = 1500L

class SplashScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val flowNavigator = getFlowNavigator()

        var imageVisible by remember { mutableStateOf(false) }
        val imageAlpha: Float by animateFloatAsState(
            targetValue = if (imageVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION_MS,
                easing = LinearEasing
            )
        )

        LaunchedEffect("init") {
            imageVisible = true

            delay(SPLASH_DURATION_MS)
            flowNavigator.newRoot(HomeFlow())
        }

        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp),
                alpha = imageAlpha,
                painter = painterResource(Res.drawable.ic_compose),
                contentDescription = null
            )
        }
    }
}