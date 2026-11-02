package ru.rznnike.demokmp.app.common.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val DEFAULT_PROGRESS_DELAY_MS = 250L

abstract class BaseUiViewModel<State> : BaseViewModel() {
    protected val mutableUiState by lazy { MutableStateFlow(provideDefaultUIState()) }
    val uiState: StateFlow<State> by lazy { mutableUiState.asStateFlow() }

    protected open var progressDelayMs: Long = DEFAULT_PROGRESS_DELAY_MS
    private var progressJob: Job? = null
    private var goalProgressState = false

    protected fun setProgress(show: Boolean, immediately: Boolean = false) {
        if ((goalProgressState == show) && (progressJob != null) && (!immediately)) return

        goalProgressState = show
        progressJob?.cancel()
        if (immediately || (progressDelayMs <= 0)) {
            onProgressStateChanged(show)
            progressJob = null
        } else {
            progressJob = viewModelScope.launch {
                delay(progressDelayMs.milliseconds)
                onProgressStateChanged(show)
                progressJob = null
            }
        }
    }

    protected open fun onProgressStateChanged(show: Boolean) = Unit

    protected abstract fun provideDefaultUIState(): State
}