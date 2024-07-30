package ru.rznnike.demokmp.app.navigation

import cafe.adriel.voyager.navigator.Navigator

class ScreenNavigator(
    private val navigator: Navigator
) {
    fun open(screen: NavigationScreen) = navigator.push(screen)

    fun open(screens: List<NavigationScreen>) = navigator.push(screens)

    fun replace(screen: NavigationScreen) = navigator.replace(screen)

    fun newRoot(screen: NavigationScreen) = navigator.replaceAll(screen)

    fun close() = if (navigator.size > 1) {
        navigator.pop()
    } else {
        FlowNavigator(navigator.parent!!).close()
    }
}