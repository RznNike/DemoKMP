package ru.rznnike.demokmp.app.ui.screen.comobjectexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.ui.screen.navigation.NavigationExampleScreen

class ComObjectExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(ComObjectExampleScreen())
}