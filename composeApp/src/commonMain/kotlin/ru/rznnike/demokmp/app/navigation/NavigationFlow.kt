package ru.rznnike.demokmp.app.navigation

abstract class NavigationFlow {
    abstract val screens: List<NavigationScreen>

    fun toInfo() = NavigationFlowInfo(
        type = this::class.qualifiedName ?: "",
        screenCount = screens.size
    )
}