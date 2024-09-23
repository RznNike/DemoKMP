package ru.rznnike.demokmp.app.ui.screen.httpexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class HTTPExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(HTTPExampleScreen())
}