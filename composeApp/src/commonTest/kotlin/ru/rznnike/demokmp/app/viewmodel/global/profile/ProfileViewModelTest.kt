package ru.rznnike.demokmp.app.viewmodel.global.profile

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [ProfileViewModel] holds no collaborators and starts no coroutines, so no Koin module and no
 * `Dispatchers.setMain` is needed — the state is a plain Compose `mutableStateOf`.
 */
class ProfileViewModelTest {
    @Test
    fun `name defaults to the predefined value`() {
        // Act
        val viewModel = ProfileViewModel()

        // Assert
        assertEquals("John Wick", viewModel.nameInput)
    }

    @Test
    fun `onNameInput replaces the current name`() {
        // Arrange
        val viewModel = ProfileViewModel()

        // Act
        viewModel.onNameInput("Neo")

        // Assert
        assertEquals("Neo", viewModel.nameInput)
    }

    @Test
    fun `onNameInput accepts an empty value`() {
        // Arrange
        val viewModel = ProfileViewModel()

        // Act
        viewModel.onNameInput("")

        // Assert (no validation is applied at this layer)
        assertEquals("", viewModel.nameInput)
    }
}
