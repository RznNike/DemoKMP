package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.rznnike.demokmp.app.navigation.navtype.LogNetworkMessageNavType
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import kotlin.reflect.typeOf

val LocalNavController = staticCompositionLocalOf { NavController() }
val LocalNavigationStructure = staticCompositionLocalOf { mutableListOf<Int>() }

@Composable
fun createNavHost(flow: NavigationFlow) {
    val navController = rememberNavController()
    val navigationStructure = rememberSaveable { mutableListOf(flow.screens.size) }
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalNavigationStructure provides navigationStructure
    ) {
        NavHost(
            navController = navController,
            startDestination = flow.screens.first()
        ) {
            buildNavGraph()
        }

        val navigator = getNavigator()
        LaunchedEffect(Unit) {
            if (flow.screens.size > 1) {
                navigator.openScreens(
                    flow.screens.subList(1, flow.screens.size)
                )
            }
        }
    }
}

@Composable
fun getNavigator(): FlowNavigator {
    val windowConfigurationViewModel = windowViewModel<WindowConfigurationViewModel>()
    val windowConfigurationUiState by windowConfigurationViewModel.uiState.collectAsState()
    return FlowNavigator(
        navController = LocalNavController.current,
        navigationStructure = LocalNavigationStructure.current,
        closeWindowCallback = windowConfigurationUiState.closeWindowCallback
    )
}

expect fun NavGraphBuilder.buildNavGraph()

val navGraphTypeMap = mapOf(
    typeOf<LogNetworkMessage>() to LogNetworkMessageNavType
)

inline fun <reified T : NavigationScreen> NavGraphBuilder.addToNavGraph() {
    composable<T>(
        typeMap = navGraphTypeMap
    ) { backStackEntry ->
        val screen: T = backStackEntry.toRoute()
        screen.Content()
    }
}