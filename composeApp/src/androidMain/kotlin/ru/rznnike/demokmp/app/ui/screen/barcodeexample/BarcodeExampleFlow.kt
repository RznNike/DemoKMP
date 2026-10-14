package ru.rznnike.demokmp.app.ui.screen.barcodeexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class BarcodeExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(BarcodeExampleScreen())
}