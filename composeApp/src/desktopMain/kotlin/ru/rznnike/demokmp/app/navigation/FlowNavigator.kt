package ru.rznnike.demokmp.app.navigation

import cafe.adriel.voyager.navigator.Navigator
import kotlin.system.exitProcess

class FlowNavigator(
    private val navigator: Navigator
) {
    fun open(flow: NavigationFlow) = navigator.push(flow)

    fun open(flows: List<NavigationFlow>) = navigator.push(flows)

    fun replace(flow: NavigationFlow) = navigator.replace(flow)

    fun newRoot(flow: NavigationFlow) = navigator.replaceAll(flow)

    fun close() = if (navigator.size > 1) {
        navigator.pop()
    } else {
        exitProcess(0)
    }
}