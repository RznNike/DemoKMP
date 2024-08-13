package ru.rznnike.demokmp.app.ui.screen.splash

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class SplashFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(SplashScreen())
    }
}