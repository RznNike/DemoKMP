package ru.rznnike.demokmp.app.dispatcher.notifier

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import ru.rznnike.demokmp.domain.common.FakeCoroutineScopeProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.app_name
import ru.rznnike.demokmp.generated.resources.test_message
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestCoroutineScheduler.createNotifier() = Notifier(
    FakeCoroutineScopeProvider(
        uiDispatcher = StandardTestDispatcher(this),
        ioDispatcher = UnconfinedTestDispatcher(this)
    )
)

@OptIn(ExperimentalCoroutinesApi::class)
class NotifierTest {
    private fun TestScope.collectMessages(notifier: Notifier): MutableList<SystemMessage> {
        val received = mutableListOf<SystemMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { received += it }
        }
        return received
    }

    @Test
    fun `sendMessage with text emits a bar message`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)

        // Act
        notifier.sendMessage("hello")

        // Assert
        assertEquals(
            listOf(SystemMessage(text = "hello", type = SystemMessage.Type.BAR)),
            received
        )
    }

    @Test
    fun `sendMessage with a string resource emits a bar message`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)

        // Act
        notifier.sendMessage(Res.string.test_message)

        // Assert
        val message = received.single()
        assertEquals(Res.string.test_message, message.textRes)
        assertEquals(SystemMessage.Type.BAR, message.type)
    }

    @Test
    fun `sendAlert with text emits an alert message`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)

        // Act
        notifier.sendAlert("boom")

        // Assert
        assertEquals(
            listOf(SystemMessage(text = "boom", type = SystemMessage.Type.ALERT)),
            received
        )
    }

    @Test
    fun `sendAlert with a string resource emits an alert message`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)

        // Act
        notifier.sendAlert(Res.string.test_message)

        // Assert
        val message = received.single()
        assertEquals(Res.string.test_message, message.textRes)
        assertEquals(SystemMessage.Type.ALERT, message.type)
    }

    @Test
    fun `sendActionMessage with text emits action text and callback`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)
        var actionInvoked = false

        // Act
        notifier.sendActionMessage(
            text = "message",
            actionText = "action"
        ) { actionInvoked = true }

        // Assert
        val message = received.single()
        assertEquals("message", message.text)
        assertEquals("action", message.actionText)
        assertNull(message.textRes)
        assertEquals(SystemMessage.Type.BAR, message.type)
        message.actionCallback?.invoke()
        assertTrue(actionInvoked)
    }

    @Test
    fun `sendActionMessage with string resources emits resource texts and callback`() = runTest {
        // Arrange
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)
        var actionInvoked = false

        // Act
        notifier.sendActionMessage(
            textRes = Res.string.app_name,
            actionTextRes = Res.string.test_message
        ) { actionInvoked = true }

        // Assert
        val message = received.single()
        assertEquals(Res.string.app_name, message.textRes)
        assertEquals(Res.string.test_message, message.actionTextRes)
        assertNull(message.text)
        assertEquals(SystemMessage.Type.BAR, message.type)
        message.actionCallback?.invoke()
        assertTrue(actionInvoked)
    }

    @Test
    fun `messages sent before subscribing are not replayed`() = runTest {
        // Arrange
        val notifier = Notifier(FakeCoroutineScopeProvider())
        notifier.sendMessage("before")

        // Act
        val received = collectMessages(notifier)
        notifier.sendMessage("after")

        // Assert
        assertEquals(listOf("after"), received.map { it.text })
    }

    @Test
    fun `messages are emitted on the io scope`() = runTest {
        // Arrange (ui is deferred, so a synchronous delivery proves the io scope is used)
        val notifier = testScheduler.createNotifier()
        val received = collectMessages(notifier)

        // Act
        notifier.sendMessage("hello")

        // Assert
        assertEquals(1, received.size)
    }
}
