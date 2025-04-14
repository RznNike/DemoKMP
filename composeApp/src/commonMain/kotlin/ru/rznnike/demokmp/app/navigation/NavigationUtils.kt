package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.FadeTransition
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel

val LocalNavigationStructure = staticCompositionLocalOf { mutableListOf<Int>() }

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun createNavigator(flow: NavigationFlow) {
    val navigationStructure = rememberSaveable { mutableListOf(flow.screens.size) }
    CompositionLocalProvider(
        LocalNavigationStructure provides navigationStructure
    ) {
        Navigator(
            screens = flow.screens,
            disposeBehavior = NavigatorDisposeBehavior(disposeSteps = false)
        ) { navigator ->
            FadeTransition(
                navigator = navigator,
                disposeScreenAfterTransitionEnd = true
            )
        }
    }
}

@Composable
fun getNavigator(): FlowNavigator {
    val windowConfigurationViewModel = windowViewModel<WindowConfigurationViewModel>()
    val windowConfigurationUiState by windowConfigurationViewModel.uiState.collectAsState()
    return FlowNavigator(
        navigator = LocalNavigator.currentOrThrow,
        navigationStructure = LocalNavigationStructure.current,
        closeWindowCallback = windowConfigurationUiState.closeWindowCallback
    )
}