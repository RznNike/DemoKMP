package ru.rznnike.demokmp.app.dispatcher.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent.BottomStatusBarRequested
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent.LoggerWindowRequested
import ru.rznnike.demokmp.app.dispatcher.event.AppEvent.RestartRequested
import ru.rznnike.demokmp.domain.common.FakeCoroutineScopeProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class RecordingListener(
    private val customTag: String? = null
) : EventDispatcher.EventListener {
    val events = mutableListOf<AppEvent>()

    override fun onEvent(event: AppEvent) {
        events += event
    }

    override fun getTag(): String = customTag ?: "recording-listener"
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestCoroutineScheduler.createEventDispatcher() = EventDispatcher(
    FakeCoroutineScopeProvider(
        uiDispatcher = UnconfinedTestDispatcher(this),
        ioDispatcher = StandardTestDispatcher(this)
    )
)

class EventDispatcherTest {
    @Test
    fun `listener receives only the subscribed event types`() = runTest {
        // Arrange
        val dispatcher = testScheduler.createEventDispatcher()
        val listener = RecordingListener()
        dispatcher.addEventListener(RestartRequested::class, listener)

        // Act
        dispatcher.sendEvent(BottomStatusBarRequested(show = false))
        dispatcher.sendEvent(RestartRequested)
        dispatcher.sendEvent(LoggerWindowRequested)

        // Assert
        assertEquals(listOf<AppEvent>(RestartRequested), listener.events)
    }

    @Test
    fun `listener registered for several event classes receives all of them`() = runTest {
        // Arrange
        val dispatcher = testScheduler.createEventDispatcher()
        val listener = RecordingListener()
        dispatcher.addEventListener(listOf(RestartRequested::class, LoggerWindowRequested::class), listener)

        // Act
        dispatcher.sendEvent(RestartRequested)
        dispatcher.sendEvent(LoggerWindowRequested)

        // Assert
        assertEquals(listOf(RestartRequested, LoggerWindowRequested), listener.events)
    }

    @Test
    fun `addSingleByTagEventListener replaces a listener with the same tag`() = runTest {
        // Arrange
        val dispatcher = testScheduler.createEventDispatcher()
        val replaced = RecordingListener("same-tag")
        val replacement = RecordingListener("same-tag")
        val otherTag = RecordingListener("other-tag")
        dispatcher.addSingleByTagEventListener(RestartRequested::class, replaced)
        dispatcher.addSingleByTagEventListener(RestartRequested::class, replacement)
        dispatcher.addSingleByTagEventListener(RestartRequested::class, otherTag)

        // Act
        dispatcher.sendEvent(RestartRequested)

        // Assert
        assertTrue(replaced.events.isEmpty())
        assertEquals(1, replacement.events.size)
        assertEquals(1, otherTag.events.size)
    }

    @Test
    fun `removeEventListener unregisters the listener from every event type`() = runTest {
        // Arrange
        val dispatcher = testScheduler.createEventDispatcher()
        val listener = RecordingListener()
        dispatcher.addEventListener(listOf(RestartRequested::class, LoggerWindowRequested::class), listener)
        dispatcher.removeEventListener(listener)

        // Act
        dispatcher.sendEvent(RestartRequested)
        dispatcher.sendEvent(LoggerWindowRequested)

        // Assert
        assertTrue(listener.events.isEmpty())
    }

    @Test
    fun `events are delivered on the ui scope`() = runTest {
        // Arrange (io is deferred, so a synchronous delivery proves the ui scope is used)
        val dispatcher = testScheduler.createEventDispatcher()
        val listener = RecordingListener()
        dispatcher.addEventListener(RestartRequested::class, listener)

        // Act
        dispatcher.sendEvent(RestartRequested)

        // Assert
        assertEquals(1, listener.events.size)
    }
}
