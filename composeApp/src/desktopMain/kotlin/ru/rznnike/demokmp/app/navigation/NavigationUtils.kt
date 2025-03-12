package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.rznnike.demokmp.app.ui.window.LocalWindowCloseCallback

val LocalNavigationFlows = staticCompositionLocalOf { mutableListOf<NavigationFlow>() }

@Composable
fun createNavigator(flow: NavigationFlow) {
    val flows = remember { mutableListOf(flow) }
    CompositionLocalProvider(
        LocalNavigationFlows provides flows
    ) {
        Navigator(flow.screens)
    }
}

@Composable
fun getNavigator() = FlowNavigator(
    navigator = LocalNavigator.currentOrThrow,
    navigationFlows = LocalNavigationFlows.current,
    closeWindowCallback = LocalWindowCloseCallback.current
)