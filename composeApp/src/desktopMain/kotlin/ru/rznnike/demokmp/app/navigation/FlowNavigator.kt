package ru.rznnike.demokmp.app.navigation

import cafe.adriel.voyager.navigator.Navigator
import kotlin.system.exitProcess

class FlowNavigator(
    private val navigator: Navigator,
    private val navigationFlows: MutableList<NavigationFlow>
) {
    // FLOWS
    fun openFlow(flow: NavigationFlow) {
        navigationFlows.add(flow)
        navigator.push(flow.screens)
    }

    fun openFlows(flows: List<NavigationFlow>) {
        navigationFlows.addAll(flows)
        navigator.push(flows.flatMap { it.screens })
    }

    fun replaceFlow(flow: NavigationFlow) {
        val oldFlow = navigationFlows.removeLast()
        repeat(oldFlow.screens.size) {
            navigator.pop()
        }
        openFlow(flow)
    }

    fun newRootFlow(flow: NavigationFlow) {
        navigationFlows.clear()
        navigationFlows.add(flow)
        navigator.replaceAll(flow.screens)
    }

    fun closeFlow() {
        val oldFlow = navigationFlows.removeLast()
        repeat(oldFlow.screens.size) {
            navigator.pop()
        }
        if (navigator.size < 1) {
            exitProcess(0)
        }
    }

    // SCREENS
    fun openScreen(screen: NavigationScreen) {
        navigationFlows.last().screens.add(screen)
        navigator.push(screen)
    }

    fun openScreens(screens: List<NavigationScreen>) {
        navigationFlows.last().screens.addAll(screens)
        navigator.push(screens)
    }

    fun replaceScreen(screen: NavigationScreen) {
        val flowScreens = navigationFlows.last().screens
        flowScreens.removeLast()
        flowScreens.add(screen)
        navigator.replace(screen)
    }

    fun newRootScreen(screen: NavigationScreen) {
        val flowScreens = navigationFlows.last().screens
        repeat(flowScreens.size) {
            navigator.pop()
        }
        flowScreens.clear()
        flowScreens.add(screen)
        navigator.push(screen)
    }

    fun closeScreen() {
        val flow = navigationFlows.last()
        if (flow.screens.size > 1) {
            flow.screens.removeLast()
            navigator.pop()
        } else {
            closeFlow()
        }
    }
}