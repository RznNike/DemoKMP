package ru.rznnike.demokmp.app.ui.screen.navigation

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class NavigationExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(NavigationExampleScreen(screenNumber = 1))
}