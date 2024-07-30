package ru.rznnike.demokmp.app.ui.splash

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.rznnike.demokmp.app.navigation.NavigationFlow

class SplashFlow : NavigationFlow {
    @Composable
    override fun Content() {
        Navigator(SplashScreen())
    }
}