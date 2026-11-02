package ru.rznnike.demokmp.app.ui.viewmodel.logger

import java.io.File
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
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
import kotlin.test.assertFalse
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
import ru.rznnike.demokmp.domain.interactor.log.ClearLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.ClearNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNewLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNewNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.SaveLogToFileUseCase
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogEvent
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString

private const val TIMESTAMP = 1_700_000_000_000L

/**
 * `LoggerViewModel` lives in `desktopMain`, so the test belongs in `desktopTest`.
 *
 * Both subscriptions capture `result.eventsFlow` while loading, so the fake gateway is wired to
 * [MutableSharedFlow]s that the test emits into, and the log data must be arranged *before* the
 * ViewModel is created.
 *
 * `getSuggestedSaveFileName()` reads the injected [Clock], so a fixed clock keeps it deterministic.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoggerViewModelTest {
    private val rule = ViewModelTestRule()
    private val clock = Clock.fixed(Instant.ofEpochMilli(TIMESTAMP), ZoneOffset.UTC)
    private lateinit var logGateway: FakeLogGateway
    private lateinit var errorHandler: FakeErrorHandler
    private lateinit var notifier: Notifier
    private lateinit var logEvents: MutableSharedFlow<LogEvent>
    private lateinit var networkLogEvents: MutableSharedFlow<LogEvent>

    private fun TestScope.collectAlerts(): MutableList<SystemMessage> {
        val received = mutableListOf<SystemMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { received += it }
        }
        return received
    }

    private fun logMessage(
        id: Long = 0,
        tag: String = "Tag",
        message: String = "message",
        type: LogType = LogType.DEFAULT,
        isCurrentSession: Boolean = true
    ) = LogMessage(
        id = id,
        type = type,
        level = LogLevel.INFO,
        timestamp = TIMESTAMP,
        tag = tag,
        message = message,
        isCurrentSession = isCurrentSession
    )

    private fun networkMessage(
        id: Long = 0,
        request: String = "request",
        response: String? = null,
        isCurrentSession: Boolean = true
    ) = testNetworkLogMessage(message = request, timestamp = TIMESTAMP).copy(
        id = id,
        isCurrentSession = isCurrentSession,
        response = response?.let {
            LogMessage(
                type = LogType.NETWORK,
                level = LogLevel.INFO,
                timestamp = TIMESTAMP,
                tag = "Tag",
                message = it
            )
        }
    )

    private fun arrangeLog(vararg messages: LogMessage) {
        logGateway.logData = LogData(log = messages.toList(), eventsFlow = logEvents)
    }

    private fun arrangeNetworkLog(vararg messages: NetworkLogMessage) {
        logGateway.networkLogData = NetworkLogData(log = messages.toList(), eventsFlow = networkLogEvents)
    }

    @BeforeTest
    fun setUp() {
        rule.start()
        logEvents = MutableSharedFlow(extraBufferCapacity = 16)
        networkLogEvents = MutableSharedFlow(extraBufferCapacity = 16)
        logGateway = FakeLogGateway().apply {
            logData = LogData(log = emptyList(), eventsFlow = logEvents)
            networkLogData = NetworkLogData(log = emptyList(), eventsFlow = networkLogEvents)
        }
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<Clock> { clock }
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<LogGateway> { logGateway }
                    single { GetLogUseCase(get(), get()) }
                    single { GetNewLogUseCase(get(), get()) }
                    single { GetNetworkLogUseCase(get(), get()) }
                    single { GetNewNetworkLogUseCase(get(), get()) }
                    single { ClearLogUseCase(get(), get()) }
                    single { ClearNetworkLogUseCase(get(), get()) }
                    single { SaveLogToFileUseCase(get(), get()) }
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
    fun `init loads both logs and applies the defaults`() = runTest(rule.dispatcher) {
        // Arrange
        val logMessage = logMessage(id = 1, message = "first")
        val networkMessage = networkMessage(id = 1)
        arrangeLog(logMessage)
        arrangeNetworkLog(networkMessage)

        // Act
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(LoggerViewModel.Tab.ALL, state.selectedTab)
        assertTrue(state.autoscroll)
        assertFalse(state.showOnlyCurrentSession)
        assertFalse(state.collapseNetworkMessages)
        assertFalse(state.filterOnlyByTag)
        assertEquals(listOf(logMessage), state.filteredLog)
        assertEquals(listOf(networkMessage), state.filteredNetworkLog)
        assertEquals("", viewModel.filterInput)
    }

    @Test
    fun `filter matches the tag case insensitively`() = runTest(rule.dispatcher) {
        // Arrange
        val tagged = logMessage(id = 1, tag = "Network", message = "body")
        val other = logMessage(id = 2, tag = "Other", message = "body")
        arrangeLog(tagged, other)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onFilterInput("netw")

        // Assert
        assertEquals(listOf(tagged), viewModel.uiState.value.filteredLog)
        assertEquals("netw", viewModel.filterInput)
    }

    @Test
    fun `filter only by tag drops message body matches`() = runTest(rule.dispatcher) {
        // Arrange
        val message = logMessage(id = 1, tag = "Tag", message = "needle in the body")
        arrangeLog(message)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        viewModel.onFilterInput("needle")
        assertEquals(listOf(message), viewModel.uiState.value.filteredLog)

        // Act
        viewModel.onFilterOnlyByTagClick()

        // Assert
        assertTrue(viewModel.uiState.value.filterOnlyByTag)
        assertEquals(emptyList(), viewModel.uiState.value.filteredLog)
    }

    @Test
    fun `show only current session filters both lists`() = runTest(rule.dispatcher) {
        // Arrange
        val currentLog = logMessage(id = 1, message = "current")
        val previousLog = logMessage(id = 2, message = "previous", isCurrentSession = false)
        val currentNetworkLog = networkMessage(id = 1, request = "current")
        val previousNetworkLog = networkMessage(id = 2, request = "previous", isCurrentSession = false)
        arrangeLog(currentLog, previousLog)
        arrangeNetworkLog(currentNetworkLog, previousNetworkLog)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onShowOnlyCurrentSessionClick()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state.showOnlyCurrentSession)
        assertEquals(listOf(currentLog), state.filteredLog)
        assertEquals(listOf(currentNetworkLog), state.filteredNetworkLog)
    }

    @Test
    fun `collapse network messages matches only the first line`() = runTest(rule.dispatcher) {
        // Arrange
        val networkTypeLog = logMessage(
            id = 1,
            message = "first line\nneedle",
            type = LogType.NETWORK
        )
        arrangeLog(networkTypeLog)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        viewModel.onFilterInput("needle")
        assertEquals(listOf(networkTypeLog), viewModel.uiState.value.filteredLog)

        // Act
        viewModel.onCollapseNetworkMessagesClick()

        // Assert
        assertTrue(viewModel.uiState.value.collapseNetworkMessages)
        assertEquals(emptyList(), viewModel.uiState.value.filteredLog)
    }

    @Test
    fun `onTabSelected switches the tab and ignores the current one`() = runTest(rule.dispatcher) {
        // Arrange
        arrangeLog(logMessage(id = 1))
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onTabSelected(LoggerViewModel.Tab.NETWORK)

        // Assert
        assertEquals(LoggerViewModel.Tab.NETWORK, viewModel.uiState.value.selectedTab)
        val before = viewModel.uiState.value
        viewModel.onTabSelected(LoggerViewModel.Tab.NETWORK)
        assertEquals(before, viewModel.uiState.value)
    }

    @Test
    fun `deleteLog clears the log on the ALL tab`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Act
        viewModel.deleteLog()
        advanceUntilIdle()

        // Assert
        assertEquals(1, logGateway.clearLogCalls)
        assertEquals(0, logGateway.clearNetworkLogCalls)
    }

    @Test
    fun `deleteLog clears the network log on the NETWORK tab`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        viewModel.onTabSelected(LoggerViewModel.Tab.NETWORK)

        // Act
        viewModel.deleteLog()
        advanceUntilIdle()

        // Assert
        assertEquals(0, logGateway.clearLogCalls)
        assertEquals(1, logGateway.clearNetworkLogCalls)
    }

    @Test
    fun `NewMessage appends the new messages requested from the last id`() = runTest(rule.dispatcher) {
        // Arrange
        val first = logMessage(id = 1, message = "first")
        val second = logMessage(id = 2, message = "second")
        arrangeLog(first, second)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        val appended = logMessage(id = 3, message = "third")
        logGateway.newLogMessages = listOf(appended)

        // Act
        logEvents.emit(LogEvent.NewMessage)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(2L), logGateway.requestedLastLogIds)
        assertEquals(listOf(first, second, appended), viewModel.uiState.value.filteredLog)
    }

    @Test
    fun `Cleanup reloads both logs`() = runTest(rule.dispatcher) {
        // Arrange
        arrangeLog(logMessage(id = 1, message = "old"))
        arrangeNetworkLog(networkMessage(id = 1, request = "old"))
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        val reloadedLog = logMessage(id = 2, message = "reloaded")
        val reloadedNetworkLog = networkMessage(id = 2, request = "reloaded")
        arrangeLog(reloadedLog)
        arrangeNetworkLog(reloadedNetworkLog)

        // Act
        logEvents.emit(LogEvent.Cleanup)
        networkLogEvents.emit(LogEvent.Cleanup)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(listOf(reloadedLog), state.filteredLog)
        assertEquals(listOf(reloadedNetworkLog), state.filteredNetworkLog)
    }

    @Test
    fun `NewNetworkMessage replaces an existing id in place and appends the new ones`() = runTest(rule.dispatcher) {
        // Arrange
        val first = networkMessage(id = 1, request = "request one")
        val second = networkMessage(id = 2, request = "request two")
        arrangeNetworkLog(first, second)
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        val updatedFirst = first.copy(request = first.request.copy(message = "updated one"))
        val appended = networkMessage(id = 3, request = "request three")
        logGateway.newNetworkLogMessages = listOf(appended)

        // Act
        networkLogEvents.emit(LogEvent.NewNetworkMessage(updatedFirst))
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(2L), logGateway.requestedLastNetworkLogIds)
        assertEquals(
            listOf(updatedFirst, second, appended),
            viewModel.uiState.value.filteredNetworkLog
        )
    }

    @Test
    fun `initial load failures are routed to the error handler and notify`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        logGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(2, errorHandler.proceededErrors.size)
        assertEquals(listOf("boom", "boom"), alerts.map { it.text })
        assertEquals(emptyList(), viewModel.uiState.value.filteredLog)
        assertEquals(emptyList(), viewModel.uiState.value.filteredNetworkLog)
    }

    @Test
    fun `saveLogToFile forwards the file to the gateway`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        val file = File("logs", "log_test.txt")

        // Act
        viewModel.saveLogToFile(file)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(file), logGateway.savedLogFiles)
    }

    @Test
    fun `saveLogToFile routes a failure to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        logGateway.errorToThrow = IllegalStateException("disk is full")

        // Act
        viewModel.saveLogToFile(File("logs", "log_test.txt"))
        advanceUntilIdle()

        // Assert
        assertEquals("disk is full", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("disk is full"), alerts.map { it.text })
        assertEquals(emptyList(), logGateway.savedLogFiles)
    }

    @Test
    fun `a failing deleteLog is silently swallowed`() = runTest(rule.dispatcher) {
        // Arrange — pins current behavior: `deleteLog()` never inspects the `UseCaseResult`
        val alerts = collectAlerts()
        val viewModel = LoggerViewModel()
        advanceUntilIdle()
        logGateway.errorToThrow = IllegalStateException("clear failed")

        // Act
        viewModel.deleteLog()
        advanceUntilIdle()

        // Assert — the fake throws before recording, so only the silence is observable here;
        // the successful clear paths are covered by the two `deleteLog` tests above.
        assertEquals(0, logGateway.clearLogCalls)
        assertEquals(emptyList(), errorHandler.proceededErrors)
        assertEquals(emptyList(), alerts)
    }

    @Test
    fun `suggested file name is derived from the injected clock`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = LoggerViewModel()
        advanceUntilIdle()

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
}
