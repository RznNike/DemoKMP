package ru.rznnike.demokmp.app.viewmodel.splash

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.AppGateway
import ru.rznnike.demokmp.domain.gateway.FakeAppGateway
import ru.rznnike.demokmp.domain.interactor.app.CheckIfAppIsAlreadyRunningUseCase

private class RecordingEventListener : EventDispatcher.EventListener {
    val events = mutableListOf<AppEvent>()

    override fun onEvent(event: AppEvent) {
        events += event
    }
}

/**
 * [SplashViewModel] gates navigation on two independent readiness flags, so the ordering of
 * "data ready" (an `init` coroutine) and "UI ready" (a caller-driven flag) is the whole contract.
 *
 * This class uses [StandardTestDispatcher], not the rule's default `UnconfinedTestDispatcher`:
 * unconfined would run the `init` coroutine to completion during construction, making "UI ready
 * before data" impossible to express.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val rule = ViewModelTestRule(StandardTestDispatcher())
    private lateinit var appGateway: FakeAppGateway
    private lateinit var eventListener: RecordingEventListener

    private val navigations = mutableListOf<SplashViewModel.NavigationCommand>()
    private var multiLaunchDialogShown = 0

    private fun createViewModel() = SplashViewModel().apply {
        setNavigationCallback { navigations += it }
        setDialogCallbacks { multiLaunchDialogShown++ }
    }

    @BeforeTest
    fun setUp() {
        rule.start()
        appGateway = FakeAppGateway()
        eventListener = RecordingEventListener()
        val eventDispatcher = EventDispatcher(rule.coroutineScopeProvider)
        eventDispatcher.addEventListener(
            appEventClasses = listOf(AppEvent.BottomStatusBarRequested::class),
            listener = eventListener
        )
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single { eventDispatcher }
                    single<AppGateway> { appGateway }
                    single { CheckIfAppIsAlreadyRunningUseCase(get(), get()) }
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
    fun `init hides the bottom status bar`() = runTest(rule.dispatcher) {
        // Act
        createViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(
            listOf<AppEvent>(AppEvent.BottomStatusBarRequested(show = false)),
            eventListener.events
        )
    }

    @Test
    fun `navigation fires when data is ready before ui`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setUiReady()

        // Assert
        assertEquals(listOf(SplashViewModel.NavigationCommand.MAIN), navigations)
    }

    @Test
    fun `navigation fires when ui is ready before data`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = createViewModel()

        // Act
        viewModel.setUiReady()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(SplashViewModel.NavigationCommand.MAIN), navigations)
    }

    @Test
    fun `only ui readiness does not navigate`() = runTest(rule.dispatcher) {
        // Arrange — the init coroutine is still queued, so data is not ready yet
        val viewModel = createViewModel()

        // Act
        viewModel.setUiReady()

        // Assert
        assertEquals(emptyList(), navigations)
    }

    @Test
    fun `repeated setUiReady does not navigate again`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setUiReady()
        viewModel.setUiReady()

        // Assert
        assertEquals(
            listOf(SplashViewModel.NavigationCommand.MAIN),
            navigations
        )
    }

    @Test
    fun `already running app shows the multi launch dialog and does not navigate`() = runTest(rule.dispatcher) {
        // Arrange
        appGateway.alreadyRunning = true

        // Act
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.setUiReady()

        // Assert
        assertEquals(1, multiLaunchDialogShown)
        assertEquals(emptyList(), navigations)
    }

    /**
     * Contract: a failing single-instance check surfaces as `UseCaseResult(data = null)`, so
     * `isAlreadyRunning == false` is false and the `else` branch shows the dialog instead of
     * navigating. This is the intended fail-closed behavior:
     * an unverifiable check is treated as "assume running" rather than risking two instances.
     * `isDataReady` stays false, so the splash screen never advances on its own.
     */
    @Test
    fun `failed single instance check shows the multi launch dialog and never navigates`() = runTest(rule.dispatcher) {
        // Arrange
        appGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.setUiReady()

        // Assert
        assertEquals(1, multiLaunchDialogShown)
        assertEquals(emptyList(), navigations)
    }
}
