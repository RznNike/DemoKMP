package ru.rznnike.demokmp.app.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class FlowNavigator(
    private val backStack: NavBackStack<NavigationScreen>,
    private val navigationStructure: MutableList<Int>,
    private val closeWindowCallback: () -> Unit
) {
    // FLOWS
    fun openFlow(flow: NavigationFlow) {
        navigationStructure.add(flow.screens.size)
        flow.screens.forEach {
            backStack.add(it)
        }
    }

    fun openFlows(flows: List<NavigationFlow>) {
        navigationStructure.addAll(flows.map { it.screens.size })
        flows.flatMap { it.screens }.forEach {
            backStack.add(it)
        }
    }

    fun replaceFlow(flow: NavigationFlow) {
        val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
        backStack.dropLast(oldFlowSize)
        openFlow(flow)
        System.gc()
    }

    fun newRootFlow(flow: NavigationFlow) {
        navigationStructure.clear()
        navigationStructure.add(flow.screens.size)
        backStack.clear()
        backStack.addAll(flow.screens)
    }

    fun closeFlow() {
        if (navigationStructure.size > 1) {
            val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
            backStack.dropLast(oldFlowSize)
        } else {
            closeWindowCallback()
        }
        System.gc()
    }

    // SCREENS
    fun openScreen(screen: NavigationScreen) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + 1
        backStack.add(screen)
    }

    fun openScreens(screens: List<NavigationScreen>) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + screens.size
        backStack.addAll(screens)
    }

    fun replaceScreen(screen: NavigationScreen) {
        backStack.dropLast(1)
        backStack.add(screen)
        System.gc()
    }

    fun newRootScreen(screen: NavigationScreen) {
        val flowSize = navigationStructure.last()
        backStack.dropLast(flowSize)
        navigationStructure[navigationStructure.lastIndex] = 1
        backStack.add(screen)
        System.gc()
    }

    fun closeScreen() {
        val flowSize = navigationStructure.last()
        if (flowSize > 1) {
            navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() - 1
            backStack.dropLast(1)
        } else {
            closeFlow()
        }
        System.gc()
    }
}