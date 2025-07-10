package ru.rznnike.demokmp.app.navigation

import androidx.navigation.NavController

class FlowNavigator(
    private val navController: NavController,
    private val navigationStructure: MutableList<Int>,
    private val closeWindowCallback: () -> Unit
) {
    // FLOWS
    fun openFlow(flow: NavigationFlow) {
        navigationStructure.add(flow.screens.size)
        flow.screens.forEach {
            navController.navigate(it)
        }
    }

    fun openFlows(flows: List<NavigationFlow>) {
        navigationStructure.addAll(flows.map { it.screens.size })
        flows.flatMap { it.screens }.forEach {
            navController.navigate(it)
        }
    }

    fun replaceFlow(flow: NavigationFlow) {
        val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
        repeat(oldFlowSize) {
            navController.popBackStack()
        }
        openFlow(flow)
        System.gc()
    }

    fun newRootFlow(flow: NavigationFlow) {
        navigationStructure.clear()
        navigationStructure.add(flow.screens.size)
        flow.screens.forEachIndexed { index, screen ->
            navController.navigate(screen) {
                if (index == 0) {
                    popUpTo(0)
                }
            }
        }
    }

    fun closeFlow() {
        if (navigationStructure.size > 1) {
            val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
            repeat(oldFlowSize) {
                navController.popBackStack()
            }
        } else {
            closeWindowCallback()
        }
        System.gc()
    }

    // SCREENS
    fun openScreen(screen: NavigationScreen) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + 1
        navController.navigate(screen)
    }

    fun openScreens(screens: List<NavigationScreen>) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + screens.size
        screens.forEach {
            navController.navigate(it)
        }
    }

    fun replaceScreen(screen: NavigationScreen) {
        navController.popBackStack()
        navController.navigate(screen)
        System.gc()
    }

    fun newRootScreen(screen: NavigationScreen) {
        val flowSize = navigationStructure.last()
        repeat(flowSize) {
            navController.popBackStack()
        }
        navigationStructure[navigationStructure.lastIndex] = 1
        navController.navigate(screen)
        System.gc()
    }

    fun closeScreen() {
        val flowSize = navigationStructure.last()
        if (flowSize > 1) {
            navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() - 1
            navController.popBackStack()
        } else {
            closeFlow()
        }
        System.gc()
    }
}