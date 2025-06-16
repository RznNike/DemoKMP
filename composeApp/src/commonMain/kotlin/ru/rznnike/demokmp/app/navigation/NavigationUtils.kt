package ru.rznnike.demokmp.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import kotlin.reflect.KType

private const val SCREEN_ANIMATION_DURATION_MS = 500

val LocalNavController = staticCompositionLocalOf { NavController() }
val LocalNavigationStructure = staticCompositionLocalOf { mutableListOf<Int>() }

@OptIn(ExperimentalComposeUiApi::class)
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
            startDestination = flow.screens.first(),
            enterTransition = { fadeIn(animationSpec = tween(SCREEN_ANIMATION_DURATION_MS)) },
            exitTransition = { fadeOut(animationSpec = tween(SCREEN_ANIMATION_DURATION_MS)) }
        ) {
            buildNavGraph()
        }
        if (OperatingSystem.isDesktop) {
            BackHandler {} // Disable default Esc handling in favor of custom hotkeys listener
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

inline fun <reified T : NavigationScreen> NavGraphBuilder.addToNavGraph(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap()
) {
    composable<T>(
        typeMap = typeMap
    ) { backStackEntry ->
        val screen: T = backStackEntry.toRoute()
        screen.Content()
    }
}