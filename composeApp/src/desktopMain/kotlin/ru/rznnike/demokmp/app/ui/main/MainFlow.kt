package ru.rznnike.demokmp.app.ui.main

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.rznnike.demokmp.app.navigation.NavigationFlow

class MainFlow : NavigationFlow {
    @Composable
    override fun Content() {
        Navigator(MainScreen())
    }
}