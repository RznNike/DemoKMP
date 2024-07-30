package ru.rznnike.demokmp.app.ui.settings

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.rznnike.demokmp.app.navigation.NavigationFlow

class SettingsFlow : NavigationFlow {
    @Composable
    override fun Content() {
        Navigator(SettingsScreen())
    }
}