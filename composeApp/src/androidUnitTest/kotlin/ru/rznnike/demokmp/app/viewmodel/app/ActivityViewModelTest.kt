package ru.rznnike.demokmp.app.viewmodel.app

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.domain.common.FakeCoroutineScopeProvider

/**
 * `ActivityViewModel` lives in `androidMain`, so this test belongs in `androidUnitTest`
 * (task `testDebugUnitTest`).
 *
 * No `ViewModelTestRule` is needed: the ViewModel never touches `viewModelScope`, so
 * `Dispatchers.Main` is never used — only [EventDispatcher], which runs its listeners on the
 * injected `CoroutineScopeProvider` (unconfined here, so delivery is synchronous).
 */
class ActivityViewModelTest {
    private lateinit var eventDispatcher: EventDispatcher

    @BeforeTest
    fun setUp() {
        eventDispatcher = EventDispatcher(FakeCoroutineScopeProvider())
        startKoin {
            modules(
                module {
                    single { eventDispatcher }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `activity restart event invokes the restart callback`() {
        // Arrange
        var restarts = 0
        ActivityViewModel { restarts++ }

        // Act
        eventDispatcher.sendEvent(AppEvent.ActivityRestartRequested)

        // Assert
        assertEquals(1, restarts)
    }

    @Test
    fun `other event types do not invoke the restart callback`() {
        // Arrange
        var restarts = 0
        ActivityViewModel { restarts++ }

        // Act
        eventDispatcher.sendEvent(AppEvent.RestartRequested)

        // Assert
        assertEquals(0, restarts)
    }
}
