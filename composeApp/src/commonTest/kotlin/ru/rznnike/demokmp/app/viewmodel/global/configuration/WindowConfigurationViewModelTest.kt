package ru.rznnike.demokmp.app.viewmodel.global.configuration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule

/**
 * [WindowConfigurationViewModel] has no collaborators, but `setWindowTitle` launches in
 * `viewModelScope`, so the Main dispatcher still has to be replaced by [ViewModelTestRule].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WindowConfigurationViewModelTest {
    private val rule = ViewModelTestRule()

    @BeforeTest
    fun setUp() = rule.start()

    @AfterTest
    fun tearDown() = rule.stop()

    @Test
    fun `default state has an empty title and a callable close callback`() {
        // Act
        val viewModel = WindowConfigurationViewModel()

        // Assert
        assertEquals("", viewModel.uiState.value.windowTitle)
        viewModel.uiState.value.closeWindowCallback()
    }

    @Test
    fun `setWindowTitle updates the title`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WindowConfigurationViewModel()

        // Act
        viewModel.setWindowTitle("DemoKMP")
        advanceUntilIdle()

        // Assert
        assertEquals("DemoKMP", viewModel.uiState.value.windowTitle)
    }

    @Test
    fun `setWindowTitle keeps only the latest title`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WindowConfigurationViewModel()

        // Act
        viewModel.setWindowTitle("first")
        viewModel.setWindowTitle("second")
        advanceUntilIdle()

        // Assert
        assertEquals("second", viewModel.uiState.value.windowTitle)
    }

    @Test
    fun `setCloseWindowCallback stores the handler in the state`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WindowConfigurationViewModel()
        val callback = {}

        // Act
        viewModel.setCloseWindowCallback(callback)
        advanceUntilIdle()

        // Assert
        assertSame(callback, viewModel.uiState.value.closeWindowCallback)
    }

    @Test
    fun `close callback is invoked through the state`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WindowConfigurationViewModel()
        var invocations = 0
        viewModel.setCloseWindowCallback { invocations++ }
        advanceUntilIdle()

        // Act (exactly what `Window(onCloseRequest = …)` does)
        viewModel.uiState.value.closeWindowCallback()

        // Assert
        assertEquals(1, invocations)
    }

    @Test
    fun `state is conflated for the same handler instance but not for a new one`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WindowConfigurationViewModel()
        val callback = {}
        val emissions = mutableListOf<WindowConfigurationViewModel.UiState>()
        backgroundScope.launch { viewModel.uiState.collect { emissions += it } }
        advanceUntilIdle()

        viewModel.setCloseWindowCallback(callback)
        advanceUntilIdle()
        assertEquals(2, emissions.size, "initial state + first registration")

        // Act (same instance — already the stored value)
        viewModel.setCloseWindowCallback(callback)
        advanceUntilIdle()

        // Assert
        assertEquals(2, emissions.size, "an equal state must not be re-emitted")

        viewModel.setCloseWindowCallback { }
        advanceUntilIdle()
        assertEquals(3, emissions.size, "a different handler instance is a new state")
    }
}
