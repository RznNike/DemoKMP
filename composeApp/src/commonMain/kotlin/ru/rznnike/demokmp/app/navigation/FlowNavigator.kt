package ru.rznnike.demokmp.app.navigation

import androidx.navigation3.runtime.NavBackStack

class FlowNavigator(
    private val backStack: NavBackStack<NavigationScreen>,
    private val navigationStructure: MutableList<NavigationFlowInfo>,
    private val closeWindowCallback: () -> Unit
) {
    // FLOWS
    fun openFlow(flow: NavigationFlow) {
        navigationStructure.add(flow.toInfo())
        flow.screens.forEach {
            backStack.add(it)
        }
    }

    @Suppress("unused")
    fun openFlows(flows: List<NavigationFlow>) {
        navigationStructure.addAll(
            flows.map { it.toInfo() }
        )
        flows.flatMap { it.screens }.forEach {
            backStack.add(it)
        }
    }

    fun replaceFlow(flow: NavigationFlow) {
        val oldFlowSize = navigationStructure.removeAt(navigationStructure.lastIndex).screenCount
        repeat(oldFlowSize) {
            backStack.removeLastOrNull()
        }
        openFlow(flow)
        System.gc()
    }

    fun newRootFlow(flow: NavigationFlow) {
        navigationStructure.clear()
        navigationStructure.add(flow.toInfo())
        backStack.clear()
        backStack.addAll(flow.screens)
    }

    fun openFlowSingle(flow: NavigationFlow) {
        val newFlowInfo = flow.toInfo()
        var indexOffset = 0
        navigationStructure.forEach { oldFlowInfo ->
            if (oldFlowInfo.type == newFlowInfo.type) {
                navigationStructure.remove(oldFlowInfo)
                repeat(oldFlowInfo.screenCount) {
                    try { backStack.removeAt(indexOffset) } catch (_: Exception) {}
                }
            } else {
                indexOffset += oldFlowInfo.screenCount
            }
        }
        openFlow(flow)
        System.gc()
    }

    fun closeFlow() {
        if (navigationStructure.size > 1) {
            val oldFlowSize = navigationStructure.removeLast().screenCount
            repeat(oldFlowSize) {
                backStack.removeLastOrNull()
            }
        } else {
            closeWindowCallback()
        }
        System.gc()
    }

    // SCREENS
    fun openScreen(screen: NavigationScreen) {
        navigationStructure.last().screenCount += 1
        backStack.add(screen)
    }

    @Suppress("unused")
    fun openScreens(screens: List<NavigationScreen>) {
        navigationStructure.last().screenCount += screens.size
        backStack.addAll(screens)
    }

    fun replaceScreen(screen: NavigationScreen) {
        backStack.removeLastOrNull()
        backStack.add(screen)
        System.gc()
    }

    fun newRootScreen(screen: NavigationScreen) {
        val flowSize = navigationStructure.last().screenCount
        repeat(flowSize) {
            backStack.removeLastOrNull()
        }
        navigationStructure.last().screenCount = 1
        backStack.add(screen)
        System.gc()
    }

    fun closeScreen() {
        val flowSize = navigationStructure.last().screenCount
        if (flowSize > 1) {
            navigationStructure.last().screenCount -= 1
            backStack.removeLastOrNull()
        } else {
            closeFlow()
        }
        System.gc()
    }
}