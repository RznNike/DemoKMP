package ru.rznnike.demokmp.app.viewmodel.pdfexample

import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePdfExampleGateway
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase

/**
 * `PdfExampleViewModel` sets `isError` **inside** the `ErrorHandler` callback rather than in its own
 * `onError` branch, so this class needs a [FakeErrorHandler] that actually invokes callbacks —
 * `FakeErrorHandler(invokeCallbacks = true)`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PdfExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var pdfGateway: FakePdfExampleGateway
    private lateinit var errorHandler: FakeErrorHandler

    @BeforeTest
    fun setUp() {
        rule.start()
        pdfGateway = FakePdfExampleGateway()
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single<PdfExampleGateway> { pdfGateway }
                    single { GetSamplePdfUseCase(get(), get()) }
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
    fun `sample pdf is loaded into the state on init`() = runTest(rule.dispatcher) {
        // Arrange
        val expected = File("sample.pdf")
        pdfGateway.samplePdf = expected

        // Act
        val viewModel = PdfExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(expected, viewModel.uiState.value.pdf)
    }

    @Test
    fun `successful load leaves the error flag false`() = runTest(rule.dispatcher) {
        // Act
        val viewModel = PdfExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.uiState.value.isError)
    }

    @Test
    fun `getSuggestedSaveFileName returns the example pdf save name`() {
        // Arrange
        val viewModel = PdfExampleViewModel()

        // Act
        val fileName = viewModel.getSuggestedSaveFileName()

        // Assert
        assertEquals(DataConstants.EXAMPLE_PDF_SAVE_NAME, fileName)
    }

    @Test
    fun `load error is routed to the error handler`() = runTest(rule.dispatcher) {
        // Arrange
        pdfGateway.errorToThrow = IllegalStateException("boom")

        // Act
        PdfExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("boom", errorHandler.proceededErrors.single().message)
    }

    @Test
    fun `load error sets the error flag through the error handler callback`() = runTest(rule.dispatcher) {
        // Arrange
        pdfGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = PdfExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.pdf)
    }
}
