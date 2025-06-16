package ru.rznnike.demokmp.app.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.rznnike.demokmp.app.ui.theme.LocalIsDarkTheme

@Serializable
abstract class AndroidNavigationScreen : NavigationScreen() {
    open val isLightStatusBar: Boolean
        @Composable
        get() = !LocalIsDarkTheme.current
    open val isLightNavigationBar: Boolean
        @Composable
        get() = !LocalIsDarkTheme.current

    private val activity: ComponentActivity
        @Composable
        get() = LocalActivity.current as ComponentActivity

    @Transient
    var onBackPressedCallback: (navigator: FlowNavigator) -> Unit = { navigator ->
        navigator.closeScreen()
    }

    @Composable
    final override fun Content() {
        SetSystemBarsColors()
        HandleBackPress()
        super.Content()
    }

    @Composable
    private fun SetSystemBarsColors() {
        activity.window?.let { window ->
            val isLightSB = isLightStatusBar
            val isLightNB = isLightNavigationBar
            LaunchedEffect(isLightSB, isLightNB) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isLightSB
                    isAppearanceLightNavigationBars = isLightNB
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