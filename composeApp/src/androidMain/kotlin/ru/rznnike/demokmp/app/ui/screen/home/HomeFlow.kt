package ru.rznnike.demokmp.app.ui.screen.home

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class HomeFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(HomeScreen())
}