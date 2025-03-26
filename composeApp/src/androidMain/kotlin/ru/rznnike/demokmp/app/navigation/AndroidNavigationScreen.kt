package ru.rznnike.demokmp.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

abstract class AndroidNavigationScreen : NavigationScreen() {
    open val isLightStatusBar: Boolean
        @Composable
        get() = !isSystemInDarkTheme()
    open val isLightNavigationBar: Boolean
        @Composable
        get() = !isSystemInDarkTheme()
    private val activity: ComponentActivity?
        @Composable
        get() = LocalContext.current as ComponentActivity?

    @Composable
    final override fun Content() {
        setSystemBarsColors()
        super.Content()
    }

    @Composable
    private fun setSystemBarsColors() {
        activity?.window?.let { window ->
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
}