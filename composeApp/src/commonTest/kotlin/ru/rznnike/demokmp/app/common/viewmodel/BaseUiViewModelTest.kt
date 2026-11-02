package ru.rznnike.demokmp.app.common.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class TestableUiViewModel(
    override var progressDelayMs: Long = 0L
) : BaseUiViewModel<Unit>() {
    val progressChanges = mutableListOf<Boolean>()

    override fun provideDefaultUIState() = Unit

    override fun onProgressStateChanged(show: Boolean) {
        progressChanges += show
    }

    fun requestProgress(show: Boolean, immediately: Boolean = false) =
        setProgress(show = show, immediately = immediately)
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseUiViewModelTest {
    private val rule = ViewModelTestRule()

    @BeforeTest
    fun setUp() = rule.start()

    @AfterTest
    fun tearDown() = rule.stop()

    @Test
    fun `immediate progress is reported synchronously`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 5_000L)

        // Act
        viewModel.requestProgress(show = true, immediately = true)

        // Assert
        assertEquals(listOf(true), viewModel.progressChanges)
    }

    @Test
    fun `zero delay reports progress synchronously`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 0L)

        // Act
        viewModel.requestProgress(show = true)

        // Assert
        assertEquals(listOf(true), viewModel.progressChanges)
    }

    @Test
    fun `delayed progress is reported only after the delay elapses`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 30L)

        // Act
        viewModel.requestProgress(show = true)

        // Assert (virtual time — nothing reported until the delay is advanced)
        assertEquals(emptyList(), viewModel.progressChanges)

        advanceUntilIdle()
        assertEquals(listOf(true), viewModel.progressChanges)
    }

    @Test
    fun `duplicate same goal request is ignored while pending`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 100L)

        // Act
        viewModel.requestProgress(show = true)
        viewModel.requestProgress(show = true)

        // Assert
        advanceUntilIdle()
        assertEquals(listOf(true), viewModel.progressChanges)
    }

    @Test
    fun `state flip cancels the pending update`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 200L)
        viewModel.requestProgress(show = true)

        // Act
        viewModel.requestProgress(show = false, immediately = true)

        // Assert
        assertEquals(listOf(false), viewModel.progressChanges)

        advanceUntilIdle()
        assertEquals(listOf(false), viewModel.progressChanges)
    }

    @Test
    fun `duplicate same goal request after completion is reported again`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 20L)
        viewModel.requestProgress(show = true)
        advanceUntilIdle()
        assertEquals(listOf(true), viewModel.progressChanges)

        // Act
        viewModel.requestProgress(show = true)

        // Assert
        advanceUntilIdle()
        assertEquals(listOf(true, true), viewModel.progressChanges)
    }

    @Test
    fun `opposite goal after completion is reported`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 20L)
        viewModel.requestProgress(show = true)
        advanceUntilIdle()

        // Act
        viewModel.requestProgress(show = false)

        // Assert
        advanceUntilIdle()
        assertEquals(listOf(true, false), viewModel.progressChanges)
    }
}
