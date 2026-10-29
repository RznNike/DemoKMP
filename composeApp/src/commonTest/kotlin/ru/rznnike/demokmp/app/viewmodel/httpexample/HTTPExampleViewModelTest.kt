package ru.rznnike.demokmp.app.viewmodel.httpexample

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeHTTPExampleGateway
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway
import ru.rznnike.demokmp.domain.interactor.httpexample.GetRandomImageLinksUseCase

/**
 * The in-flight assertions rely on [FakeHTTPExampleGateway.gate]: the gateway fake otherwise returns
 * synchronously, so `isLoading` and "previous images cleared" would be overwritten before the test
 * could observe them.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HTTPExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var httpGateway: FakeHTTPExampleGateway
    private lateinit var errorHandler: FakeErrorHandler

    @BeforeTest
    fun setUp() {
        rule.start()
        httpGateway = FakeHTTPExampleGateway()
        errorHandler = FakeErrorHandler()
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { Notifier(get()) }
                    single<HTTPExampleGateway> { httpGateway }
                    single { GetRandomImageLinksUseCase(get(), get()) }
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
    fun `init requests the configured number of images`() = runTest(rule.dispatcher) {
        // Act
        HTTPExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(6), httpGateway.requestedCounts)
    }

    @Test
    fun `images from the gateway are published to the state`() = runTest(rule.dispatcher) {
        // Arrange
        httpGateway.links = listOf("a.png", "b.png")

        // Act
        val viewModel = HTTPExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("a.png", "b.png"), viewModel.uiState.value.images)
    }

    @Test
    fun `loading is set while the request is in flight and cleared afterwards`() = runTest(rule.dispatcher) {
        // Arrange
        httpGateway.gate = CompletableDeferred()

        // Act — the gated gateway leaves the request suspended after construction
        val viewModel = HTTPExampleViewModel()
        val isLoadingDuringRequest = viewModel.uiState.value.isLoading
        httpGateway.gate?.complete(Unit)
        advanceUntilIdle()

        // Assert
        assertTrue(isLoadingDuringRequest)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `previously loaded images are cleared when a new request starts`() = runTest(rule.dispatcher) {
        // Arrange
        httpGateway.links = listOf("old.png")
        val viewModel = HTTPExampleViewModel()
        advanceUntilIdle()
        httpGateway.links = listOf("new.png")
        httpGateway.gate = CompletableDeferred()

        // Act
        viewModel.requestImages()
        val imagesDuringRequest = viewModel.uiState.value.images
        httpGateway.gate?.complete(Unit)
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList(), imagesDuringRequest)
        assertEquals(listOf("new.png"), viewModel.uiState.value.images)
    }

    @Test
    fun `load error is routed to the error handler and leaves images empty`() = runTest(rule.dispatcher) {
        // Arrange
        httpGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = HTTPExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("boom", errorHandler.proceededErrors.single().message)
        assertEquals(emptyList(), viewModel.uiState.value.images)
    }

    @Test
    fun `loading is cleared after a failed request`() = runTest(rule.dispatcher) {
        // Arrange
        httpGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = HTTPExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `requestImages issues a fresh request`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = HTTPExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.requestImages()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(6, 6), httpGateway.requestedCounts)
    }
}
