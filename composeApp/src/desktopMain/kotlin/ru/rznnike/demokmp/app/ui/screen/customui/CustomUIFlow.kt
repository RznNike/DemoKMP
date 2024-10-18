package ru.rznnike.demokmp.app.ui.screen.customui

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class CustomUIFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(CustomUIScreen())
}