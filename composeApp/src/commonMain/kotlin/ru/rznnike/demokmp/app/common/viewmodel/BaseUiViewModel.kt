package ru.rznnike.demokmp.app.common.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.concurrent.schedule

private const val DEFAULT_PROGRESS_DELAY_MS = 250L

abstract class BaseUiViewModel<State> : BaseViewModel() {
    protected val mutableUiState by lazy { MutableStateFlow(provideDefaultUIState()) }
    val uiState: StateFlow<State> by lazy { mutableUiState.asStateFlow() }

    protected open var progressDelayMs: Long = DEFAULT_PROGRESS_DELAY_MS
    private var progressTask: TimerTask? = null
    private var goalProgressState = false

    protected fun setProgress(show: Boolean, immediately: Boolean = false) {
        if ((goalProgressState == show) && (progressTask != null) && (!immediately)) return

        goalProgressState = show
        progressTask?.cancel()
        if (immediately || (progressDelayMs <= 0)) {
            onProgressStateChanged(show)
        } else {
            progressTask = Timer().schedule(progressDelayMs) {
                onProgressStateChanged(show)
            }
        }
    }

    protected open fun onProgressStateChanged(show: Boolean) = Unit

    protected abstract fun provideDefaultUIState(): State
}