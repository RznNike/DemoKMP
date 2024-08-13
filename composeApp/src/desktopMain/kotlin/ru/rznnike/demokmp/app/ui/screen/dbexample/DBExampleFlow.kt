package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.runtime.Composable
import ru.rznnike.demokmp.app.navigation.NavigationFlow
import ru.rznnike.demokmp.app.navigation.createNavigator

class DBExampleFlow : NavigationFlow() {
    @Composable
    override fun Content() {
        createNavigator(DBExampleScreen())
    }
}