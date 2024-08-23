package ru.rznnike.demokmp.app.ui.screen.wsexample

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class WebSocketsExampleFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(WebSocketsExampleScreen())
    }
}