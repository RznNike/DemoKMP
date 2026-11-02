package ru.rznnike.demokmp.app.viewmodel.settings

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var preferencesGateway: FakePreferencesGateway
    private lateinit var errorHandler: FakeErrorHandler

    @BeforeTest
    fun setUp() {
        rule.start()
        preferencesGateway = FakePreferencesGateway()
        errorHandler = FakeErrorHandler()
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { Notifier(get()) }
                    single<PreferencesGateway> { preferencesGateway }
                    single { GetTestCounterUseCase(get(), get()) }
                    single { SetTestCounterUseCase(get(), get()) }
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
    fun `counter is loaded from preferences into the state`() = runTest(rule.dispatcher) {
        // Arrange
        preferencesGateway.testCounter = 42

        // Act
        val viewModel = SettingsViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(42, viewModel.uiState.value.counter)
    }

    @Test
    fun `onCounterInput persists the value and updates the state`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = SettingsViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onCounterInput(newValue = 7)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(7), preferencesGateway.setTestCounterValues)
        assertEquals(7, viewModel.uiState.value.counter)
    }

    @Test
    fun `load error is routed to the error handler`() = runTest(rule.dispatcher) {
        // Arrange
        preferencesGateway.errorToThrow = IllegalStateException("boom")

        // Act
        SettingsViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(1, errorHandler.proceededErrors.size)
        assertEquals("boom", errorHandler.proceededErrors.single().message)
    }
}
