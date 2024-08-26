package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.FadeTransition
import ru.rznnike.demokmp.app.ui.theme.AppTheme

@Composable
fun getFlowNavigator() = FlowNavigator(LocalNavigator.currentOrThrow.parent!!)

@Composable
fun getScreenNavigator() = ScreenNavigator(LocalNavigator.currentOrThrow)

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun createNavigator(screen: Screen) = Navigator(screen) { navigator ->
    AppTheme {
        FadeTransition(
            navigator = navigator,
            disposeScreenAfterTransitionEnd = true
        )
    }
}