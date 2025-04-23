package ru.rznnike.demokmp.app.ui.screen.chartexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class ChartExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(ChartExampleScreen())
}