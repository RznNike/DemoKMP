package ru.rznnike.demokmp.app.viewmodel.language

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.domain.interactor.preferences.GetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetLanguageUseCase
import ru.rznnike.demokmp.domain.model.common.Language

class LanguageViewModel : BaseUiViewModel<LanguageViewModel.UiState>() {
    private val getLanguageUseCase: GetLanguageUseCase by inject()
    private val setLanguageUseCase: SetLanguageUseCase by inject()

    init {
        viewModelScope.launch {
            val selectedLanguage = getLanguageUseCase().data
            mutableUiState.update { currentState ->
                currentState.copy(
                    language = selectedLanguage ?: currentState.language,
                    loaded = true
                )
            }
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun setLanguage(newValue: Language) {
        viewModelScope.launch {
            setLanguageUseCase(newValue)

            mutableUiState.update { currentState ->
                currentState.copy(
                    language = newValue
                )
            }
        }
    }

    data class UiState(
        val language: Language = Language.default,
        val loaded: Boolean = false
    )
}