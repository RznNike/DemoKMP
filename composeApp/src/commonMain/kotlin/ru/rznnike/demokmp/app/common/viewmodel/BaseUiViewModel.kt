package ru.rznnike.demokmp.app.common.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseUiViewModel<State> : BaseViewModel() {
    protected val mutableUiState by lazy { MutableStateFlow(provideDefaultUIState()) }
    val uiState: StateFlow<State> by lazy { mutableUiState.asStateFlow() }

    abstract fun provideDefaultUIState(): State
}