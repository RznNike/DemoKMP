package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable

class TestScreen(
    val id: String
) : NavigationScreen() {
    @Composable
    override fun Layout() = Unit
}

class FirstTestFlow(
    override val screens: List<NavigationScreen>
) : NavigationFlow()

class SecondTestFlow(
    override val screens: List<NavigationScreen>
) : NavigationFlow()

class ThirdTestFlow(
    override val screens: List<NavigationScreen>
) : NavigationFlow()
