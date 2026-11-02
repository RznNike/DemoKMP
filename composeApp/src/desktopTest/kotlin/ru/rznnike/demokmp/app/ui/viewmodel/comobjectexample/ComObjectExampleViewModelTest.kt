package ru.rznnike.demokmp.app.ui.viewmodel.comobjectexample

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.gateway.FakeComObjectExampleGateway
import ru.rznnike.demokmp.domain.interactor.comobjectexample.GetPCDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.MinimizeAllWindowsUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.OpenFolderOrFileUseCase

/**
 * `ComObjectExampleViewModel` lives in `desktopMain` (the COM shell wrapper is desktop-only), so the
 * test belongs in `desktopTest`.
 *
 * The ViewModel only uses `viewModelScope` and `CoroutineScopeProvider` is never injected, so the Koin
 * module needs neither — [Notifier] is built directly on the rule's scope provider.
 *
 * Alerts are emitted inside the `ErrorHandler` callback, hence `FakeErrorHandler(invokeCallbacks = true)`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ComObjectExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var comObjectGateway: FakeComObjectExampleGateway
    private lateinit var errorHandler: FakeErrorHandler
    private lateinit var notifier: Notifier

    private fun TestScope.collectAlerts(): MutableList<SystemMessage> {
        val received = mutableListOf<SystemMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { received += it }
        }
        return received
    }

    @BeforeTest
    fun setUp() {
        rule.start()
        comObjectGateway = FakeComObjectExampleGateway()
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<ComObjectExampleGateway> { comObjectGateway }
                    single { GetPCDataUseCase(get(), get()) }
                    single { OpenFolderOrFileUseCase(get(), get()) }
                    single { MinimizeAllWindowsUseCase(get(), get()) }
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
    fun `init loads the PC data`() = runTest(rule.dispatcher) {
        // Arrange
        comObjectGateway.pcData = "Windows 11 Pro"

        // Act
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("Windows 11 Pro", viewModel.uiState.value.pcData)
        assertEquals(listOf("getPCData"), comObjectGateway.calls)
    }

    @Test
    fun `a failing PC data load is silently swallowed`() = runTest(rule.dispatcher) {
        // Arrange — pins current behavior: `loadData()` passes no error callback to `.process(...)`
        val alerts = collectAlerts()
        comObjectGateway.errorToThrow = IllegalStateException("wmi failed")

        // Act
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("", viewModel.uiState.value.pcData)
        assertEquals(emptyList(), errorHandler.proceededErrors)
        assertEquals(emptyList(), alerts)
    }

    @Test
    fun `onPathInput stores the value without forwarding it`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onPathInput("C:\\Temp")

        // Assert
        assertEquals("C:\\Temp", viewModel.pathInput)
        assertEquals(listOf("getPCData"), comObjectGateway.calls)
    }

    @Test
    fun `openPath with a blank input forwards nothing`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()
        viewModel.onPathInput("   ")

        // Act
        viewModel.openPath()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("getPCData"), comObjectGateway.calls)
        assertEquals(emptyList(), errorHandler.proceededErrors)
    }

    @Test
    fun `openPath forwards the path`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()
        viewModel.onPathInput("C:\\Temp")

        // Act
        viewModel.openPath()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("getPCData", "openFolderOrFile:C:\\Temp"), comObjectGateway.calls)
    }

    @Test
    fun `openPath failure is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()
        viewModel.onPathInput("C:\\Temp")
        comObjectGateway.errorToThrow = IllegalStateException("cannot open")

        // Act
        viewModel.openPath()
        advanceUntilIdle()

        // Assert
        assertEquals("cannot open", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("cannot open"), alerts.map { it.text })
        assertEquals(listOf("getPCData"), comObjectGateway.calls)
    }

    @Test
    fun `minimizeAllWindows forwards the command`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.minimizeAllWindows()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("getPCData", "minimizeAllWindows"), comObjectGateway.calls)
    }

    @Test
    fun `minimizeAllWindows failure is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = ComObjectExampleViewModel()
        advanceUntilIdle()
        comObjectGateway.errorToThrow = IllegalStateException("cannot minimize")

        // Act
        viewModel.minimizeAllWindows()
        advanceUntilIdle()

        // Assert
        assertEquals("cannot minimize", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("cannot minimize"), alerts.map { it.text })
        assertEquals(listOf("getPCData"), comObjectGateway.calls)
    }
}
