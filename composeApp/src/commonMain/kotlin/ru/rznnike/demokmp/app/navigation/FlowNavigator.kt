package ru.rznnike.demokmp.app.navigation

import cafe.adriel.voyager.navigator.Navigator

class FlowNavigator(
    private val navigator: Navigator,
    private val navigationStructure: MutableList<Int>,
    private val closeWindowCallback: () -> Unit
) {
    // FLOWS
    fun openFlow(flow: NavigationFlow) {
        navigationStructure.add(flow.screens.size)
        navigator.push(flow.screens)
    }

    fun openFlows(flows: List<NavigationFlow>) {
        navigationStructure.addAll(flows.map { it.screens.size })
        navigator.push(flows.flatMap { it.screens })
    }

    fun replaceFlow(flow: NavigationFlow) {
        val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
        repeat(oldFlowSize) {
            navigator.pop()
        }
        openFlow(flow)
    }

    fun newRootFlow(flow: NavigationFlow) {
        navigationStructure.clear()
        navigationStructure.add(flow.screens.size)
        navigator.replaceAll(flow.screens)
    }

    fun closeFlow() {
        if (navigationStructure.size > 1) {
            val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex)
            repeat(oldFlowSize) {
                navigator.pop()
            }
        } else {
            closeWindowCallback()
        }
    }

    // SCREENS
    fun openScreen(screen: NavigationScreen) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + 1
        navigator.push(screen)
    }

    fun openScreens(screens: List<NavigationScreen>) {
        navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() + screens.size
        navigator.push(screens)
    }

    fun replaceScreen(screen: NavigationScreen) {
        navigator.replace(screen)
    }

    fun newRootScreen(screen: NavigationScreen) {
        val flowSize = navigationStructure.last()
        repeat(flowSize) {
            navigator.pop()
        }
        navigationStructure[navigationStructure.lastIndex] = 1
        navigator.push(screen)
    }

    fun closeScreen() {
        val flowSize = navigationStructure.last()
        if (flowSize > 1) {
            navigationStructure[navigationStructure.lastIndex] = navigationStructure.last() - 1
            navigator.pop()
        } else {
            closeFlow()
        }
    }
}