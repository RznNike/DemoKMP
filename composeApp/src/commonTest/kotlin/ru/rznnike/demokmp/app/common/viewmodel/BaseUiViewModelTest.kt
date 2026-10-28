package ru.rznnike.demokmp.app.common.viewmodel

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val AWAIT_TIMEOUT_MS = 2_000L
private const val NO_CHANGE_WINDOW_MS = 400L

private class TestableUiViewModel(
    override var progressDelayMs: Long = 0L
) : BaseUiViewModel<Unit>() {
    private val progressChanges = LinkedBlockingQueue<Boolean>()

    override fun provideDefaultUIState() = Unit

    override fun onProgressStateChanged(show: Boolean) {
        progressChanges.add(show)
    }

    fun requestProgress(show: Boolean, immediately: Boolean = false) =
        setProgress(show = show, immediately = immediately)

    fun awaitProgressChange(timeoutMs: Long = AWAIT_TIMEOUT_MS): Boolean =
        requireNotNull(progressChanges.poll(timeoutMs, TimeUnit.MILLISECONDS)) {
            "no progress change reported within $timeoutMs ms"
        }

    fun awaitNoProgressChange(timeoutMs: Long = NO_CHANGE_WINDOW_MS): Boolean =
        progressChanges.poll(timeoutMs, TimeUnit.MILLISECONDS) == null
}

class BaseUiViewModelTest {
    @Test
    fun `immediate progress is reported synchronously`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 5_000L)

        // Act
        viewModel.requestProgress(show = true, immediately = true)

        // Assert
        assertEquals(true, viewModel.awaitProgressChange(timeoutMs = 0L))
    }

    @Test
    fun `zero delay reports progress synchronously`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 0L)

        // Act
        viewModel.requestProgress(show = true)

        // Assert
        assertEquals(true, viewModel.awaitProgressChange(timeoutMs = 0L))
    }

    @Test
    fun `delayed progress is reported after the delay`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 30L)

        // Act
        viewModel.requestProgress(show = true)

        // Assert
        assertEquals(true, viewModel.awaitProgressChange())
    }

    @Test
    fun `duplicate same goal request is ignored while pending`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 100L)

        // Act
        viewModel.requestProgress(show = true)
        viewModel.requestProgress(show = true)

        // Assert
        assertEquals(true, viewModel.awaitProgressChange())
        assertTrue(viewModel.awaitNoProgressChange())
    }

    @Test
    fun `state flip cancels the pending update`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 200L)
        viewModel.requestProgress(show = true)

        // Act
        viewModel.requestProgress(show = false, immediately = true)

        // Assert
        assertEquals(false, viewModel.awaitProgressChange())
        assertTrue(viewModel.awaitNoProgressChange())
    }

    @Test
    fun `duplicate same goal request after completion is reported again`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 20L)
        viewModel.requestProgress(show = true)
        assertEquals(true, viewModel.awaitProgressChange())
        // the timer task clears the pending task right after the callback fires
        assertTrue(viewModel.awaitNoProgressChange(timeoutMs = 50L))

        // Act
        viewModel.requestProgress(show = true)

        // Assert
        assertEquals(true, viewModel.awaitProgressChange())
    }

    @Test
    fun `opposite goal after completion is reported`() {
        // Arrange
        val viewModel = TestableUiViewModel(progressDelayMs = 20L)
        viewModel.requestProgress(show = true)
        assertEquals(true, viewModel.awaitProgressChange())

        // Act
        viewModel.requestProgress(show = false)

        // Assert
        assertEquals(false, viewModel.awaitProgressChange())
    }
}
