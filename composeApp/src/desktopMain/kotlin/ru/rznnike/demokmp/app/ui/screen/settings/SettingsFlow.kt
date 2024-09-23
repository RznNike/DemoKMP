package ru.rznnike.demokmp.app.ui.screen.settings

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class SettingsFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(SettingsScreen())
}