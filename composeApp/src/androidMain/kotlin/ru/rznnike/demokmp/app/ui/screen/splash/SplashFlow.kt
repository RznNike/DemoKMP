package ru.rznnike.demokmp.app.ui.screen.splash

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class SplashFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(SplashScreen())
}