package ru.rznnike.demokmp.app.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey

abstract class NavigationFlow : Screen {
    override val key: ScreenKey = uniqueScreenKey
}