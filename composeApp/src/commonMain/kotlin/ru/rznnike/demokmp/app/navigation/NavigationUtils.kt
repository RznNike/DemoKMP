package ru.rznnike.demokmp.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.WindowConfigurationViewModel
import ru.rznnike.demokmp.domain.utils.OperatingSystem

private const val SCREEN_ANIMATION_DURATION_MS = 500

val LocalBackStack = staticCompositionLocalOf { NavBackStack<NavigationScreen>() }
val LocalNavigationStructure = staticCompositionLocalOf { mutableListOf<Int>() }

@Composable
fun CreateNavDisplay(flow: NavigationFlow) {
    val config = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
//                subclass()
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    val backStack = rememberNavBackStack(
        configuration = config,
        elements = flow.screens.toTypedArray()
    ) as NavBackStack<NavigationScreen>
    val navigationStructure = rememberSaveable { mutableListOf(flow.screens.size) }
    CompositionLocalProvider(
        LocalBackStack provides backStack,
        LocalNavigationStructure provides navigationStructure
    ) {
        val transition = ContentTransform(
            targetContentEnter = fadeIn(animationSpec = tween(SCREEN_ANIMATION_DURATION_MS)),
            initialContentExit = fadeOut(animationSpec = tween(SCREEN_ANIMATION_DURATION_MS))
        )
        NavDisplay(
            backStack = backStack,
            onBack = {
                // Disable default Esc handling in favor of custom hotkeys listener
                if (!OperatingSystem.isDesktop) backStack.removeLastOrNull()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = { transition },
            popTransitionSpec = { transition },
            entryProvider = { screen ->
                NavEntry(screen) { screen.Content() }
            }
        )
    }
}

@Composable
fun getNavigator(): FlowNavigator {
    val windowConfigurationViewModel = windowViewModel<WindowConfigurationViewModel>()
    val windowConfigurationUiState by windowConfigurationViewModel.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val navigationStructure = LocalNavigationStructure.current
    return remember {
        FlowNavigator(
            backStack = backStack,
            navigationStructure = navigationStructure,
            closeWindowCallback = windowConfigurationUiState.closeWindowCallback
        )
    }
}