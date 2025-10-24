package ru.rznnike.demokmp.app.viewmodel.global.configuration

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.interactor.app.CloseAppSingleInstanceSocketUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.DestroyShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.InitShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.CloseDBUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetThemeUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetThemeUseCase
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error_restart_from_ide
import java.io.File
import java.util.*

class AppConfigurationViewModel : BaseUiViewModel<AppConfigurationViewModel.UiState>() {
    private val eventDispatcher: EventDispatcher by inject()
    private val notifier: Notifier by inject()
    private val getLanguageUseCase: GetLanguageUseCase by inject()
    private val setLanguageUseCase: SetLanguageUseCase by inject()
    private val getThemeUseCase: GetThemeUseCase by inject()
    private val setThemeUseCase: SetThemeUseCase by inject()
    private val initShellWrapperUseCase: InitShellWrapperUseCase by inject()
    private val destroyShellWrapperUseCase: DestroyShellWrapperUseCase by inject()
    private val closeDBUseCase: CloseDBUseCase by inject()
    private val closeAppSingleInstanceSocketUseCase: CloseAppSingleInstanceSocketUseCase by inject()

    private var closeAppCallback: (() -> Unit)? = null

    private val eventListener = object : EventDispatcher.EventListener {
        override fun onEvent(event: AppEvent) {
            when (event) {
                is AppEvent.RestartRequested -> {
                    closeApplication(isRestart = true)
                }
                else -> Unit
            }
        }
    }

    init {
        subscribeToEvents()
        initLanguageAndTheme()
        initShellWrapper()
    }

    override fun onCleared() {
        eventDispatcher.removeEventListener(eventListener)
    }

    override fun provideDefaultUIState() = UiState()

    private fun subscribeToEvents() {
        eventDispatcher.addEventListener(
            appEventClasses = listOf(
                AppEvent.RestartRequested::class
            ),
            listener = eventListener
        )
    }

    private fun initLanguageAndTheme() {
        viewModelScope.launch {
            val selectedLanguage = getLanguageUseCase().data ?: Language.default
            val selectedTheme = getThemeUseCase().data ?: Theme.default
            applySelectedLanguage(selectedLanguage)

            mutableUiState.update { currentState ->
                currentState.copy(
                    language = selectedLanguage,
                    theme = selectedTheme,
                    isLoaded = true
                )
            }
        }
    }

    private fun initShellWrapper() {
        viewModelScope.launch {
            initShellWrapperUseCase()
        }
    }

    fun setLanguage(newValue: Language) {
        viewModelScope.launch {
            if (newValue != mutableUiState.value.language) {
                setLanguageUseCase(newValue)
                applySelectedLanguage(newValue)

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

    fun setArgs(newValue: Array<String>) {
        viewModelScope.launch {
            mutableUiState.update { currentState ->
                currentState.copy(
                    args = newValue.toList()
                )
            }
        }
    }

    fun setCloseAppCallback(newValue: () -> Unit) {
        closeAppCallback = newValue
    }

    fun closeApplication(isRestart: Boolean = false) {
        viewModelScope.launch {
            if (BuildKonfig.RUN_FROM_IDE && isRestart) {
                notifier.sendAlert(Res.string.error_restart_from_ide)
                return@launch
            }

            eventDispatcher.removeEventListener(eventListener)
            destroyShellWrapperUseCase()
            closeDBUseCase()
            closeAppSingleInstanceSocketUseCase()
            Logger.i("Application finish\n")
            delay(100)

            if (OperatingSystem.isDesktop || (!isRestart)) {
                closeAppCallback?.invoke()
            }
            if (isRestart) {
                relaunchApplication()
            }
        }
    }

    private fun relaunchApplication() {
        if (OperatingSystem.isAndroid) {
            eventDispatcher.sendEvent(AppEvent.ActivityRestartRequested)
        } else {
            ProcessBuilder(
                if (OperatingSystem.isLinux) {
                    listOf("sh", "./${DataConstants.RUN_SCRIPT_NAME}")
                } else {
                    listOf("wscript", DataConstants.RUN_SCRIPT_NAME)
                }
            )
                .directory(File(DataConstants.ROOT_DIR))
                .start()
        }
    }

    private fun applySelectedLanguage(language: Language) {
        if (OperatingSystem.isDesktop) {
            Locale.setDefault(Locale.forLanguageTag(language.fullTag))
        }
    }

    data class UiState(
        val args: List<String> = emptyList(),
        val language: Language = Language.default,
        val theme: Theme = Theme.default,
        val isLoaded: Boolean = false
    )
}