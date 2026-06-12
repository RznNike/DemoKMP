package ru.rznnike.demokmp.app.ui.screen.markdown

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class MarkdownExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(MarkdownExampleScreen())
}