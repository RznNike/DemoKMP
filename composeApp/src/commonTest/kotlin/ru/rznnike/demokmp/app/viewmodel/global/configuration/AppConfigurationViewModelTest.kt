package ru.rznnike.demokmp.app.viewmodel.global.configuration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.*
import ru.rznnike.demokmp.domain.interactor.app.CloseAppSingleInstanceSocketUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.DestroyShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.InitShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.CloseDBUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.*
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import kotlin.test.*

private class RecordingEventListener : EventDispatcher.EventListener {
    val events = mutableListOf<AppEvent>()

    override fun onEvent(event: AppEvent) {
        events += event
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppConfigurationViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var preferencesGateway: FakePreferencesGateway
    private lateinit var comObjectGateway: FakeComObjectExampleGateway
    private lateinit var dbGateway: FakeDBExampleGateway
    private lateinit var appGateway: FakeAppGateway
    private lateinit var eventDispatcher: EventDispatcher
    private lateinit var notifier: Notifier
    private lateinit var eventListener: RecordingEventListener

    @BeforeTest
    fun setUp() {
        rule.start()
        preferencesGateway = FakePreferencesGateway()
        comObjectGateway = FakeComObjectExampleGateway()
        dbGateway = FakeDBExampleGateway()
        appGateway = FakeAppGateway()
        eventDispatcher = EventDispatcher(rule.coroutineScopeProvider)
        notifier = Notifier(rule.coroutineScopeProvider)
        eventListener = RecordingEventListener()
        eventDispatcher.addEventListener(
            appEventClasses = listOf(
                AppEvent.RestartRequested::class,
                AppEvent.BottomStatusBarRequested::class,
                AppEvent.LoggerWindowRequested::class
            ),
            listener = eventListener
        )
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { FakeErrorHandler() }
                    single { notifier }
                    single { eventDispatcher }
                    single<PreferencesGateway> { preferencesGateway }
                    single<ComObjectExampleGateway> { comObjectGateway }
                    single<DBExampleGateway> { dbGateway }
                    single<AppGateway> { appGateway }
                    single { GetLanguageUseCase(get(), get()) }
                    single { SetLanguageUseCase(get(), get()) }
                    single { GetThemeUseCase(get(), get()) }
                    single { SetThemeUseCase(get(), get()) }
                    single { GetUiScaleUseCase(get(), get()) }
                    single { SetUiScaleUseCase(get(), get()) }
                    single { InitShellWrapperUseCase(get(), get()) }
                    single { DestroyShellWrapperUseCase(get(), get()) }
                    single { CloseDBUseCase(get(), get()) }
                    single { CloseAppSingleInstanceSocketUseCase(get(), get()) }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        rule.stop()
    }

    @Test
    fun `defaults are loaded when preferences return defaults`() = runTest(rule.dispatcher) {
        // Arrange - gateways return defaults
        preferencesGateway.language = Language.EN
        preferencesGateway.theme = Theme.AUTO
        preferencesGateway.uiScale = UiScale.S100

        // Act
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(Language.EN, viewModel.uiState.value.language)
        assertEquals(Theme.AUTO, viewModel.uiState.value.theme)
        assertEquals(UiScale.S100, viewModel.uiState.value.uiScale)
        assertTrue(viewModel.uiState.value.isLoaded)
        assertFalse(viewModel.uiState.value.isBottomStatusBarVisible)
        assertFalse(viewModel.uiState.value.isAppClosing)
        assertEquals(emptyList(), viewModel.uiState.value.args)
    }

    @Test
    fun `defaults used when preferences return null-like defaults`() = runTest(rule.dispatcher) {
        // Arrange
        preferencesGateway.language = Language.default
        preferencesGateway.theme = Theme.default
        preferencesGateway.uiScale = UiScale.default

        // Act
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(Language.default, viewModel.uiState.value.language)
        assertEquals(Theme.default, viewModel.uiState.value.theme)
        assertEquals(UiScale.default, viewModel.uiState.value.uiScale)
        assertTrue(viewModel.uiState.value.isLoaded)
    }

    @Test
    fun `setLanguage persists and updates state only when changed`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        assertEquals(Language.EN, viewModel.uiState.value.language)

        // Act - change language
        viewModel.setLanguage(Language.RU)
        advanceUntilIdle()

        // Assert
        assertEquals(Language.RU, viewModel.uiState.value.language)
        assertEquals(listOf(Language.RU), preferencesGateway.setLanguageValues)

        // Act - set same value again
        viewModel.setLanguage(Language.RU)
        advanceUntilIdle()

        // Assert - should not persist again
        assertEquals(listOf(Language.RU), preferencesGateway.setLanguageValues)
    }

    @Test
    fun `setTheme persists and updates state only when changed`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        assertEquals(Theme.AUTO, viewModel.uiState.value.theme)

        // Act - change theme
        viewModel.setTheme(Theme.DARK)
        advanceUntilIdle()

        // Assert
        assertEquals(Theme.DARK, viewModel.uiState.value.theme)
        assertEquals(listOf(Theme.DARK), preferencesGateway.setThemeValues)

        // Act - set same value again
        viewModel.setTheme(Theme.DARK)
        advanceUntilIdle()

        // Assert - should not persist again
        assertEquals(listOf(Theme.DARK), preferencesGateway.setThemeValues)
    }

    @Test
    fun `setUiScale persists and updates state only when changed`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        assertEquals(UiScale.S100, viewModel.uiState.value.uiScale)

        // Act - change ui scale
        viewModel.setUiScale(UiScale.S120)
        advanceUntilIdle()

        // Assert
        assertEquals(UiScale.S120, viewModel.uiState.value.uiScale)
        assertEquals(listOf(UiScale.S120), preferencesGateway.setUiScaleValues)

        // Act - set same value again
        viewModel.setUiScale(UiScale.S120)
        advanceUntilIdle()

        // Assert - should not persist again
        assertEquals(listOf(UiScale.S120), preferencesGateway.setUiScaleValues)
    }

    @Test
    fun `setArgs updates state`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setArgs(arrayOf("--test", "value"))
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("--test", "value"), viewModel.uiState.value.args)
    }

    @Test
    fun `BottomStatusBarRequested event toggles bottom status bar visibility`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isBottomStatusBarVisible)

        // Act
        eventDispatcher.sendEvent(AppEvent.BottomStatusBarRequested(show = true))
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value.isBottomStatusBarVisible)

        // Act
        eventDispatcher.sendEvent(AppEvent.BottomStatusBarRequested(show = false))
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.uiState.value.isBottomStatusBarVisible)
    }

    @Test
    fun `closeApplication sets isAppClosing and runs close sequence`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var callbackInvoked = false
        viewModel.setCloseAppCallback { callbackInvoked = true }

        // Act
        viewModel.closeApplication(isRestart = false)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value.isAppClosing)
        assertEquals(1, comObjectGateway.calls.count { it == "destroyShellWrapper" })
        assertEquals(1, dbGateway.closeDBCalls)
        assertEquals(1, appGateway.closeSingleInstanceSocketCalls)
        assertTrue(callbackInvoked)
    }

    @Test
    fun `closeApplication is guarded against re-entry`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var callbackInvocations = 0
        viewModel.setCloseAppCallback { callbackInvocations++ }

        // Act - first close
        viewModel.closeApplication(isRestart = false)
        advanceUntilIdle()

        // Act - second close should be ignored
        viewModel.closeApplication(isRestart = false)
        advanceUntilIdle()

        // Assert
        assertEquals(1, callbackInvocations)
        assertEquals(1, comObjectGateway.calls.count { it == "destroyShellWrapper" })
        assertEquals(1, dbGateway.closeDBCalls)
    }

    @Test
    fun `forceCloseApplication invokes callback without close sequence`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var callbackInvoked = false
        viewModel.setCloseAppCallback { callbackInvoked = true }

        // Act
        viewModel.forceCloseApplication()

        // Assert
        assertTrue(callbackInvoked)
        assertFalse(viewModel.uiState.value.isAppClosing)
    }

    @Test
    fun `logger window callback is invoked`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var loggerOpened = false
        viewModel.setLoggerWindowCallback { loggerOpened = true }

        // Act
        viewModel.openLoggerWindow()

        // Assert
        assertTrue(loggerOpened)
    }

    @Test
    fun `hotkeys dialog callback is invoked`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var dialogOpened = false
        viewModel.setHotkeysDialogCallback { dialogOpened = true }

        // Act
        viewModel.openHotkeysDialog()

        // Assert
        assertTrue(dialogOpened)
    }

    @Test
    fun `LoggerWindowRequested event opens logger window`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = AppConfigurationViewModel()
        advanceUntilIdle()
        var loggerOpened = false
        viewModel.setLoggerWindowCallback { loggerOpened = true }

        // Act
        eventDispatcher.sendEvent(AppEvent.LoggerWindowRequested)
        advanceUntilIdle()

        // Assert
        assertTrue(loggerOpened)
    }
}
