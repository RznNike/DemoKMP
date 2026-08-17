package ru.rznnike.demokmp.app.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.rznnike.demokmp.app.ui.theme.LocalIsDarkTheme

@Serializable
abstract class AndroidNavigationScreen : NavigationScreen() {
    var isLightStatusBar by mutableStateOf(false)
    var isLightNavigationBar by mutableStateOf(false)

    private val activity: ComponentActivity
        @Composable
        get() = LocalActivity.current as ComponentActivity

    @Transient
    var onBackPressedCallback: (navigator: FlowNavigator) -> Unit = { navigator ->
        navigator.closeScreen()
    }

    @Composable
    final override fun Content() {
        InitSystemBarsColors()
        ApplySystemBarsColors()
        HandleBackPress()
        super.Content()
    }

    @Composable
    open fun InitSystemBarsColors() {
        val isDarkTheme = LocalIsDarkTheme.current
        LaunchedEffect(isDarkTheme) {
            isLightStatusBar = !isDarkTheme
            isLightNavigationBar = !isDarkTheme
        }
    }

    @Composable
    private fun ApplySystemBarsColors() {
        activity.window?.let { window ->
            LaunchedEffect(isLightStatusBar, isLightNavigationBar) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isLightStatusBar
                    isAppearanceLightNavigationBars = isLightNavigationBar
                }
            }
        }
    }

    @Composable
    private fun HandleBackPress() {
        val navigator = getNavigator()
        BackHandler {
            onBackPressedCallback(navigator)
        }
    }
}