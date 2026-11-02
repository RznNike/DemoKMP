package ru.rznnike.demokmp.app.viewmodel.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [NavigationExampleViewModel] only maps its constructor argument into the initial state and
 * increments a counter — no Koin, no coroutines, so no `ViewModelTestRule` is needed.
 */
class NavigationExampleViewModelTest {
    @Test
    fun `screen number is exposed in the initial state`() {
        // Act
        val viewModel = NavigationExampleViewModel(screenNumber = 3)

        // Assert
        assertEquals(3, viewModel.uiState.value.screenNumber)
    }

    @Test
    fun `counter starts at zero`() {
        // Act
        val viewModel = NavigationExampleViewModel(screenNumber = 1)

        // Assert
        assertEquals(0, viewModel.uiState.value.counter)
    }

    @Test
    fun `increaseCounter increments the counter`() {
        // Arrange
        val viewModel = NavigationExampleViewModel(screenNumber = 1)

        // Act
        viewModel.increaseCounter()

        // Assert
        assertEquals(1, viewModel.uiState.value.counter)
    }

    @Test
    fun `increaseCounter accumulates across calls`() {
        // Arrange
        val viewModel = NavigationExampleViewModel(screenNumber = 1)

        // Act
        repeat(3) { viewModel.increaseCounter() }

        // Assert
        assertEquals(3, viewModel.uiState.value.counter)
    }

    @Test
    fun `increaseCounter leaves the screen number untouched`() {
        // Arrange
        val viewModel = NavigationExampleViewModel(screenNumber = 7)

        // Act
        viewModel.increaseCounter()

        // Assert
        assertEquals(7, viewModel.uiState.value.screenNumber)
    }
}
