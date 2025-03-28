package ru.rznnike.demokmp.app.ui.screen.wsexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class WebSocketsExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(WebSocketsExampleScreen())
}