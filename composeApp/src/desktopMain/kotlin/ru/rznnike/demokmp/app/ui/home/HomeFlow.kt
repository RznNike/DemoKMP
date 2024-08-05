package ru.rznnike.demokmp.app.ui.home

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class HomeFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(HomeScreen())
    }
}