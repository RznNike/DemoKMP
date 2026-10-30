package ru.rznnike.demokmp.app.ui.viewmodel.logger.network

import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.dispatcher.notifier.SystemMessage
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageAsFlowUseCase
import ru.rznnike.demokmp.domain.interactor.log.SaveTextToFileUseCase
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString

private const val TIMESTAMP = 1_700_000_000_000L
private const val RESPONSE_OFFSET_MS = 1_000L

/**
 * `NetworkLogDetailsViewModel` lives in `desktopMain` (its file-saving collaborator takes a `File`),
 * so the test belongs in `desktopTest`.
 *
 * The ViewModel subscribes to [FakeLogGateway.networkMessageFlow] in `init`, so the fake is wired to a
 * [MutableSharedFlow] the test emits into. Only messages that already carry a response are applied to
 * the state, which is why the emit-based tests build messages with `response != null`.
 *
 * Timestamps are formatted with `SimpleDateFormat` in the default timezone, so every expected text is
 * derived from [toDateString] rather than hardcoded.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NetworkLogDetailsViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var logGateway: FakeLogGateway
    private lateinit var errorHandler: FakeErrorHandler
    private lateinit var notifier: Notifier
    private lateinit var messages: MutableSharedFlow<NetworkLogMessage?>

    private fun TestScope.collectAlerts(): MutableList<SystemMessage> {
        val received = mutableListOf<SystemMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { received += it }
        }
        return received
    }

    /** Mirrors `getFormattedText`: the timestamp line, a newline, then the formatted message body. */
    private fun formatted(timestamp: Long, message: String) =
        "${timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS)}\n$message"

    private fun networkMessage(
        request: String,
        requestTimestamp: Long = TIMESTAMP,
        response: String? = null,
        responseTimestamp: Long = requestTimestamp + RESPONSE_OFFSET_MS
    ): NetworkLogMessage {
        val base = testNetworkLogMessage(message = request, timestamp = requestTimestamp)
        return response?.let {
            base.copy(
                response = LogMessage(
                    type = LogType.NETWORK,
                    level = LogLevel.INFO,
                    timestamp = responseTimestamp,
                    tag = "Tag",
                    message = it
                )
            )
        } ?: base
    }

    @BeforeTest
    fun setUp() {
        rule.start()
        messages = MutableSharedFlow(extraBufferCapacity = 16)
        logGateway = FakeLogGateway().apply { networkMessageFlow = this@NetworkLogDetailsViewModelTest.messages }
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<LogGateway> { logGateway }
                    single { GetLogNetworkMessageAsFlowUseCase(get(), get()) }
                    single { SaveTextToFileUseCase(get(), get()) }
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
    fun `init seeds the state from the initial message`() = runTest(rule.dispatcher) {
        // Arrange
        val initMessage = networkMessage(request = "  request\ttext  ")

        // Act
        val viewModel = NetworkLogDetailsViewModel(initMessage)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(initMessage, state.networkLogMessage)
        assertEquals(formatted(TIMESTAMP, "request    text"), state.requestFormattedText)
        assertNull(state.responseFormattedText)
        assertEquals(0L, state.queryMatches)
        assertEquals("", viewModel.queryInput)
        assertEquals(listOf(initMessage.uuid), logGateway.flowRequestedUuids)
    }

    @Test
    fun `init formats an already present response`() = runTest(rule.dispatcher) {
        // Arrange
        val initMessage = networkMessage(request = "request", response = "response")

        // Act
        val viewModel = NetworkLogDetailsViewModel(initMessage)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(formatted(TIMESTAMP, "request"), state.requestFormattedText)
        assertEquals(
            formatted(TIMESTAMP + RESPONSE_OFFSET_MS, "response"),
            state.responseFormattedText
        )
    }

    @Test
    fun `a late response refreshes the message and both texts`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "request"))
        advanceUntilIdle()
        val updated = networkMessage(
            request = "updated request",
            requestTimestamp = TIMESTAMP + 500L,
            response = "updated response"
        )

        // Act
        messages.emit(updated)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(updated, state.networkLogMessage)
        assertEquals(formatted(TIMESTAMP + 500L, "updated request"), state.requestFormattedText)
        assertEquals(
            formatted(TIMESTAMP + 500L + RESPONSE_OFFSET_MS, "updated response"),
            state.responseFormattedText
        )
    }

    @Test
    fun `a message without a response is ignored`() = runTest(rule.dispatcher) {
        // Arrange
        val initMessage = networkMessage(request = "request")
        val viewModel = NetworkLogDetailsViewModel(initMessage)
        advanceUntilIdle()
        val before = viewModel.uiState.value

        // Act
        messages.emit(networkMessage(request = "other request"))
        advanceUntilIdle()

        // Assert
        assertEquals(before, viewModel.uiState.value)
        assertEquals(listOf(initMessage.uuid), logGateway.flowRequestedUuids)
    }

    @Test
    fun `a late response recounts the current query`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "hello"))
        advanceUntilIdle()
        viewModel.onQueryInput("hello")
        assertEquals(1L, viewModel.uiState.value.queryMatches)

        // Act
        messages.emit(networkMessage(request = "hello hello", response = "hello"))
        advanceUntilIdle()

        // Assert
        assertEquals(3L, viewModel.uiState.value.queryMatches)
        assertEquals("hello", viewModel.queryInput)
    }

    @Test
    fun `query matching is case insensitive`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "Hello HELLO"))

        // Act
        viewModel.onQueryInput("hello")

        // Assert
        assertEquals(2L, viewModel.uiState.value.queryMatches)
    }

    @Test
    fun `query matching treats the input literally`() = runTest(rule.dispatcher) {
        // Arrange — as a regex, "a.b" would also match "axb"
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "axb a.b"))

        // Act
        viewModel.onQueryInput("a.b")

        // Assert
        assertEquals(1L, viewModel.uiState.value.queryMatches)
    }

    @Test
    fun `query matching counts request and response occurrences`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(
            networkMessage(request = "match", response = "match match")
        )

        // Act
        viewModel.onQueryInput("match")

        // Assert
        assertEquals(3L, viewModel.uiState.value.queryMatches)
    }

    @Test
    fun `an empty query resets the match count`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "match"))
        viewModel.onQueryInput("match")
        assertEquals(1L, viewModel.uiState.value.queryMatches)

        // Act
        viewModel.onQueryInput("")

        // Assert
        assertEquals(0L, viewModel.uiState.value.queryMatches)
        assertEquals("", viewModel.queryInput)
    }

    @Test
    fun `suggested file name is derived from the request timestamp`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "request"))

        // Act
        val fileName = viewModel.getSuggestedSaveFileName()

        // Assert
        assertEquals(
            DataConstants.LOG_FILE_NAME_TEMPLATE.format(
                TIMESTAMP.toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
            ),
            fileName
        )
        assertTrue(fileName.startsWith("log_"))
    }

    @Test
    fun `saveLogToFile writes the full text of the message`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "  request\ttext  "))
        val file = File("logs", "log_test.txt")

        // Act
        viewModel.saveLogToFile(file)
        advanceUntilIdle()

        // Assert
        val (savedFile, savedText) = logGateway.savedTexts.single()
        assertEquals(file, savedFile)
        assertEquals(viewModel.uiState.value.networkLogMessage.getFullText(), savedText)
        // A response-less message formats identically through both code paths.
        assertEquals(viewModel.uiState.value.requestFormattedText, savedText)
    }

    @Test
    fun `saveLogToFile routes a failure to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = NetworkLogDetailsViewModel(networkMessage(request = "request"))
        logGateway.errorToThrow = IllegalStateException("disk is full")

        // Act
        viewModel.saveLogToFile(File("logs", "log_test.txt"))
        advanceUntilIdle()

        // Assert
        assertEquals("disk is full", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("disk is full"), alerts.map { it.text })
        assertEquals(emptyList(), logGateway.savedTexts)
    }
}
