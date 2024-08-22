package ru.rznnike.demokmp.app.ui.screen.httpexample

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class HTTPExampleFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(HTTPExampleScreen())
    }
}