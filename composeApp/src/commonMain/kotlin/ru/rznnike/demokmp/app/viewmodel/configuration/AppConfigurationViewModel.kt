package ru.rznnike.demokmp.app.viewmodel.configuration

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.preferences.GetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetThemeUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetThemeUseCase
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

class AppConfigurationViewModel : BaseUiViewModel<AppConfigurationViewModel.UiState>() {
    private val dispatcherProvider: DispatcherProvider by inject()
    private val getLanguageUseCase: GetLanguageUseCase by inject()
    private val setLanguageUseCase: SetLanguageUseCase by inject()
    private val getThemeUseCase: GetThemeUseCase by inject()
    private val setThemeUseCase: SetThemeUseCase by inject()

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            val selectedLanguage = getLanguageUseCase().data
            val selectedTheme = getThemeUseCase().data
            mutableUiState.update { currentState ->
                currentState.copy(
                    language = selectedLanguage ?: currentState.language,
                    theme = selectedTheme ?: currentState.theme,
                    loaded = true
                )
            }
        }
    }

    override fun provideDefaultUIState() = UiState()

    fun setLanguage(newValue: Language) {
        viewModelScope.launch {
            if (newValue != mutableUiState.value.language) {
                setLanguageUseCase(newValue)

                mutableUiState.update { currentState ->
                    currentState.copy(
                        language = newValue
                    )
                }
            }
        }
    }

    fun setTheme(newValue: Theme) {
        viewModelScope.launch {
            if (newValue != mutableUiState.value.theme) {
                setThemeUseCase(newValue)

                mutableUiState.update { currentState ->
                    currentState.copy(
                        theme = newValue
                    )
                }
            }
        }
    }

    data class UiState(
        val language: Language = Language.default,
        val theme: Theme = Theme.default,
        val loaded: Boolean = false
    )
}