package ru.rznnike.demokmp.app.ui.viewmodel.common.print

import java.io.File
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
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeFileGateway
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.gateway.FileGateway
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.interactor.file.CopyFileUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint

/**
 * `FilePrintViewModel` lives in `desktopMain` (`java.io.File` in `saveFile`), so the test belongs in
 * `desktopTest`.
 *
 * `saveFile()` uses `coroutineScopeProvider.io` while the settings work uses `viewModelScope`, so both
 * providers come from [ViewModelTestRule] and share its scheduler.
 *
 * Alerts are emitted inside the `ErrorHandler` callback, hence `FakeErrorHandler(invokeCallbacks = true)`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FilePrintViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var preferencesGateway: FakePreferencesGateway
    private lateinit var fileGateway: FakeFileGateway
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
        preferencesGateway = FakePreferencesGateway()
        fileGateway = FakeFileGateway()
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<PreferencesGateway> { preferencesGateway }
                    single<FileGateway> { fileGateway }
                    single { GetPrintSettingsUseCase(get(), get()) }
                    single { SetPrintSettingsUseCase(get(), get()) }
                    single { CopyFileUseCase(get(), get()) }
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
    fun `init loads the print settings`() = runTest(rule.dispatcher) {
        // Arrange
        val stored = PrintSettings(
            printerName = "HP LaserJet",
            twoSidedPrint = TwoSidedPrint.TWO_SIDED_LONG_EDGE
        )
        preferencesGateway.printSettings = stored

        // Act
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(stored, viewModel.uiState.value.printSettings)
    }

    @Test
    fun `a failing settings load is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        preferencesGateway.errorToThrow = IllegalStateException("prefs are broken")

        // Act
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("prefs are broken", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("prefs are broken"), alerts.map { it.text })
        assertEquals(PrintSettings(), viewModel.uiState.value.printSettings)
    }

    @Test
    fun `onTwoSidedPrintChanged skips an unchanged value`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onTwoSidedPrintChanged(TwoSidedPrint.default)
        advanceUntilIdle()

        // Assert
        assertEquals(PrintSettings(), viewModel.uiState.value.printSettings)
        assertEquals(emptyList(), preferencesGateway.setPrintSettingsValues)
    }

    @Test
    fun `onTwoSidedPrintChanged updates the state and persists it`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()
        val expected = PrintSettings(twoSidedPrint = TwoSidedPrint.TWO_SIDED_SHORT_EDGE)

        // Act
        viewModel.onTwoSidedPrintChanged(TwoSidedPrint.TWO_SIDED_SHORT_EDGE)
        advanceUntilIdle()

        // Assert
        assertEquals(expected, viewModel.uiState.value.printSettings)
        assertEquals(listOf(expected), preferencesGateway.setPrintSettingsValues)
    }

    @Test
    fun `onPrinterSelected skips an unchanged value`() = runTest(rule.dispatcher) {
        // Arrange
        preferencesGateway.printSettings = PrintSettings(printerName = "HP LaserJet")
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onPrinterSelected("HP LaserJet")
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList(), preferencesGateway.setPrintSettingsValues)
    }

    @Test
    fun `onPrinterSelected updates the state and persists it`() = runTest(rule.dispatcher) {
        // Arrange
        preferencesGateway.printSettings = PrintSettings(printerName = "HP LaserJet")
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()
        val expected = PrintSettings(printerName = "Canon")

        // Act
        viewModel.onPrinterSelected("Canon")
        advanceUntilIdle()

        // Assert
        assertEquals(expected, viewModel.uiState.value.printSettings)
        assertEquals(listOf(expected), preferencesGateway.setPrintSettingsValues)
    }

    @Test
    fun `a failing save notifies and keeps the optimistic state`() = runTest(rule.dispatcher) {
        // Arrange — pins the optimistic update: state changes before the save is attempted
        val alerts = collectAlerts()
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()
        preferencesGateway.errorToThrow = IllegalStateException("cannot save")

        // Act
        viewModel.onPrinterSelected("Canon")
        advanceUntilIdle()

        // Assert
        assertEquals("cannot save", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("cannot save"), alerts.map { it.text })
        assertEquals("Canon", viewModel.uiState.value.printSettings.printerName)
        assertEquals(emptyList(), preferencesGateway.setPrintSettingsValues)
    }

    @Test
    fun `saveFile copies the file through the gateway`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()
        val original = File("source", "document.pdf")
        val copy = File("target", "document.pdf")

        // Act
        viewModel.saveFile(original = original, copy = copy)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(original to copy), fileGateway.copiedFiles)
    }

    @Test
    fun `saveFile failure is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = FilePrintViewModel()
        advanceUntilIdle()
        fileGateway.errorToThrow = IllegalStateException("copy failed")

        // Act
        viewModel.saveFile(
            original = File("source", "document.pdf"),
            copy = File("target", "document.pdf")
        )
        advanceUntilIdle()

        // Assert
        assertEquals("copy failed", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("copy failed"), alerts.map { it.text })
        assertEquals(emptyList(), fileGateway.copiedFiles)
    }
}
