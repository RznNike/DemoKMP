package ru.rznnike.demokmp.app.ui.screen.logger

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class LoggerFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(LoggerScreen())
}