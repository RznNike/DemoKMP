package ru.rznnike.demokmp.app.ui.main

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class MainFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(MainScreen())
    }
}