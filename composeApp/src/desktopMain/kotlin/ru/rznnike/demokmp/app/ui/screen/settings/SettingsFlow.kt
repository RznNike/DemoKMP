package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class SettingsFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(SettingsScreen())
    }
}