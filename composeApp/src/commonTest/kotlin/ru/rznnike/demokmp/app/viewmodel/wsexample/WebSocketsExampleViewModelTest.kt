package ru.rznnike.demokmp.app.viewmodel.wsexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.dispatcher.notifier.SystemMessage
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeWebSocketExampleGateway
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.GetAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

/**
 * The ViewModel subscribes to the session flows in `init`, so the fake gateway is wired to a
 * [MutableSharedFlow] (incoming messages) and a [MutableStateFlow] (connection state) that the test
 * drives directly.
 *
 * Alerts are emitted from inside the `ErrorHandler` callback, so this class needs a
 * [FakeErrorHandler] that actually invokes callbacks — `FakeErrorHandler(invokeCallbacks = true)`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketsExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var wsGateway: FakeWebSocketExampleGateway
    private lateinit var errorHandler: FakeErrorHandler
    private lateinit var notifier: Notifier
    private lateinit var messages: MutableSharedFlow<WebSocketMessage>
    private lateinit var connectionState: MutableStateFlow<WebSocketConnectionState>

    private fun TestScope.collectAlerts(): MutableList<SystemMessage> {
        val received = mutableListOf<SystemMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { received += it }
        }
        return received
    }

    private fun createViewModel(store: ViewModelStore): WebSocketsExampleViewModel =
        ViewModelProvider.create(
            store,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                    WebSocketsExampleViewModel() as T
            }
        )[WebSocketsExampleViewModel::class]

    @BeforeTest
    fun setUp() {
        rule.start()
        messages = MutableSharedFlow(extraBufferCapacity = 16)
        connectionState = MutableStateFlow(WebSocketConnectionState.CONNECTED)
        wsGateway = FakeWebSocketExampleGateway().apply {
            session = WebSocketSessionData(
                url = "wss://example.com",
                messages = this@WebSocketsExampleViewModelTest.messages,
                connectionState = this@WebSocketsExampleViewModelTest.connectionState
            )
        }
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<WebSocketExampleGateway> { wsGateway }
                    single { GetAppWSSessionUseCase(get(), get()) }
                    single { CloseAppWSSessionUseCase(get(), get()) }
                    single { SendAppWSMessageUseCase(get(), get()) }
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
    fun `init subscribes and mirrors the connection state`() = runTest(rule.dispatcher) {
        // Act
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()
        connectionState.value = WebSocketConnectionState.CONNECTING
        advanceUntilIdle()

        // Assert
        assertEquals(WebSocketConnectionState.CONNECTING, viewModel.uiState.value.connectionState)
    }

    @Test
    fun `incoming messages are appended to the state`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()
        val first = WebSocketMessage(text = "one", isIncoming = true)
        val second = WebSocketMessage(text = "two", isIncoming = true)

        // Act
        messages.emit(first)
        messages.emit(second)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(first, second), viewModel.uiState.value.messages)
    }

    @Test
    fun `CLOSED cancels both collectors`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()
        connectionState.value = WebSocketConnectionState.CLOSED
        advanceUntilIdle()

        // Act — neither flow should reach the state anymore
        messages.emit(WebSocketMessage(text = "after-close", isIncoming = true))
        connectionState.value = WebSocketConnectionState.CONNECTED
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList(), viewModel.uiState.value.messages)
        assertEquals(WebSocketConnectionState.CLOSED, viewModel.uiState.value.connectionState)
    }

    @Test
    fun `onMessageInput stores the value`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onMessageInput("hello")

        // Assert
        assertEquals("hello", viewModel.messageInput)
    }

    @Test
    fun `sendMessage with a blank input does not forward anything`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onMessageInput("   ")
        viewModel.sendMessage()
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList(), wsGateway.sentMessages)
        assertEquals("   ", viewModel.messageInput)
        assertEquals(emptyList(), viewModel.uiState.value.messages)
    }

    @Test
    fun `sendMessage forwards the message, clears the input and appends it`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()
        val outgoing = WebSocketMessage(text = "hello", isIncoming = false)

        // Act
        viewModel.onMessageInput("hello")
        viewModel.sendMessage()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(outgoing), wsGateway.sentMessages)
        assertEquals("", viewModel.messageInput)
        assertEquals(listOf(outgoing), viewModel.uiState.value.messages)
    }

    @Test
    fun `session error is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        wsGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("boom", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("boom"), alerts.map { it.text })
        assertEquals(WebSocketConnectionState.DISCONNECTED, viewModel.uiState.value.connectionState)
    }

    @Test
    fun `send error notifies and keeps the input`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = WebSocketsExampleViewModel()
        advanceUntilIdle()
        viewModel.onMessageInput("hello")
        wsGateway.errorToThrow = IllegalStateException("boom")

        // Act
        viewModel.sendMessage()
        advanceUntilIdle()

        // Assert
        assertEquals("hello", viewModel.messageInput)
        assertEquals(emptyList(), viewModel.uiState.value.messages)
        assertEquals("boom", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("boom"), alerts.map { it.text })
    }

    @Test
    fun `onCleared closes the session`() = runTest(rule.dispatcher) {
        // Arrange
        val store = ViewModelStore()
        createViewModel(store)
        advanceUntilIdle()

        // Act
        store.clear()
        advanceUntilIdle()

        // Assert
        assertEquals(1, wsGateway.closeSessionCalls)
    }
}
