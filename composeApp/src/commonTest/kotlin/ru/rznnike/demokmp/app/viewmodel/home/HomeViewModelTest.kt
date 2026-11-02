package ru.rznnike.demokmp.app.viewmodel.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider

private class RecordingEventListener : EventDispatcher.EventListener {
    val events = mutableListOf<AppEvent>()

    override fun onEvent(event: AppEvent) {
        events += event
    }
}

/**
 * `openSourceCodeLink()` is intentionally not covered: it needs `getString(Res.string.…)` and the
 * platform `openLink` actual, neither of which is available in `commonTest`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var eventDispatcher: EventDispatcher
    private lateinit var eventListener: RecordingEventListener

    @BeforeTest
    fun setUp() {
        rule.start()
        eventDispatcher = EventDispatcher(rule.coroutineScopeProvider)
        eventListener = RecordingEventListener()
        // Subscribed to every event type, so the assertions can pin the complete event sequence.
        eventDispatcher.addEventListener(
            appEventClasses = listOf(
                AppEvent.RestartRequested::class,
                AppEvent.ActivityRestartRequested::class,
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
                    single { Notifier(get()) }
                    single { eventDispatcher }
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
    fun `init requests the bottom status bar without dispatching anything else`() = runTest(rule.dispatcher) {
        // Act
        HomeViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(
            listOf<AppEvent>(AppEvent.BottomStatusBarRequested(show = true)),
            eventListener.events
        )
    }

    @Test
    fun `restartApp dispatches a restart request`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = HomeViewModel()
        advanceUntilIdle()

        // Act
        viewModel.restartApp()
        advanceUntilIdle()

        // Assert
        assertEquals(
            listOf(
                AppEvent.BottomStatusBarRequested(show = true),
                AppEvent.RestartRequested
            ),
            eventListener.events
        )
    }
}
