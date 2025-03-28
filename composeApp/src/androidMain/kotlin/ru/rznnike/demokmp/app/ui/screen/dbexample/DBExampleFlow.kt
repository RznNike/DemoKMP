package ru.rznnike.demokmp.app.ui.screen.dbexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class DBExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(DBExampleScreen())
}