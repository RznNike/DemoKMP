package ru.rznnike.demokmp.app.ui.screen.pdfexample

import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.NavigationScreen

class PdfExampleFlow : NavigationFlow() {
    override val screens: MutableList<NavigationScreen> = mutableListOf(PdfExampleScreen())
}