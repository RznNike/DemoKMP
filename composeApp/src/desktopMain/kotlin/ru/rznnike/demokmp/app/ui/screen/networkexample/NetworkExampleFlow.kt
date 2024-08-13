package ru.rznnike.demokmp.app.ui.screen.networkexample

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class NetworkExampleFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(NetworkExampleScreen())
    }
}