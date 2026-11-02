package ru.rznnike.demokmp.app.viewmodel.dbexample

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.gateway.FakeDBExampleGateway
import ru.rznnike.demokmp.domain.interactor.dbexample.AddDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteAllDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.GetDBExampleDataListUseCase
import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.time.Duration.Companion.milliseconds

/** `BaseUiViewModel` default progress delay — `DBExampleViewModel` does not ask for immediate updates. */
private const val PROGRESS_DELAY_MS = 300L

/**
 * `DBExampleViewModel` calls `setProgress(show)` **without** `immediately = true`, so `isLoading`
 * reaches the state only after the 250 ms default delay — in-flight assertions advance virtual time
 * past it while [FakeDBExampleGateway.gate] holds the list request.
 *
 * Alerts are emitted from inside the `ErrorHandler` callback, so this class needs a
 * [FakeErrorHandler] that actually invokes callbacks — `FakeErrorHandler(invokeCallbacks = true)`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DBExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var dbGateway: FakeDBExampleGateway
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
        dbGateway = FakeDBExampleGateway()
        errorHandler = FakeErrorHandler(invokeCallbacks = true)
        notifier = Notifier(rule.coroutineScopeProvider)
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { notifier }
                    single<DBExampleGateway> { dbGateway }
                    single { GetDBExampleDataListUseCase(get(), get()) }
                    single { AddDBExampleDataUseCase(get(), get()) }
                    single { DeleteDBExampleDataUseCase(get(), get()) }
                    single { DeleteAllDBExampleDataUseCase(get(), get()) }
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
    fun `init loads the rows into the state`() = runTest(rule.dispatcher) {
        // Arrange
        val rows = listOf(
            DBExampleData(id = 1, name = "first"),
            DBExampleData(id = 2, name = "second")
        )
        dbGateway.allResult = rows

        // Act
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(rows, viewModel.uiState.value.data)
        assertEquals(1, dbGateway.getAllCalls)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loading is reported while the initial load is in flight`() = runTest(rule.dispatcher) {
        // Arrange
        dbGateway.gate = CompletableDeferred()

        // Act
        val viewModel = DBExampleViewModel()
        advanceTimeBy(PROGRESS_DELAY_MS.milliseconds)
        val isLoadingDuringLoad = viewModel.uiState.value.isLoading
        dbGateway.gate?.complete(Unit)
        advanceUntilIdle()

        // Assert
        assertTrue(isLoadingDuringLoad)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onNameInput stores the value`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.onNameInput("hello")

        // Assert
        assertEquals("hello", viewModel.nameInput)
    }

    @Test
    fun `addData with a blank name does not forward anything`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Act
        listOf("", " ", "   ").forEach { blank ->
            viewModel.onNameInput(blank)
            viewModel.addData()
            advanceUntilIdle()
        }

        // Assert
        assertEquals(emptyList(), dbGateway.addedData)
        assertEquals(1, dbGateway.getAllCalls)
        assertEquals("   ", viewModel.nameInput)
    }

    @Test
    fun `addData forwards the row, clears the input and reloads`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()
        viewModel.onNameInput("hello")

        // Act
        viewModel.addData()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(DBExampleData(name = "hello")), dbGateway.addedData)
        assertEquals("", viewModel.nameInput)
        assertEquals(2, dbGateway.getAllCalls)
    }

    @Test
    fun `addData is ignored while loading is in progress`() = runTest(rule.dispatcher) {
        // Arrange
        dbGateway.gate = CompletableDeferred()
        val viewModel = DBExampleViewModel()
        advanceTimeBy(PROGRESS_DELAY_MS.milliseconds)
        viewModel.onNameInput("hello")

        // Act
        viewModel.addData()
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList(), dbGateway.addedData)
        assertEquals("hello", viewModel.nameInput)

        dbGateway.gate?.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun `deleteData forwards the row and reloads`() = runTest(rule.dispatcher) {
        // Arrange
        val row = DBExampleData(id = 7, name = "row")
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.deleteData(row)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(row), dbGateway.deletedData)
        assertEquals(2, dbGateway.getAllCalls)
    }

    @Test
    fun `deleteAllData clears the table and reloads`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Act
        viewModel.deleteAllData()
        advanceUntilIdle()

        // Assert
        assertEquals(1, dbGateway.deleteAllCalls)
        assertEquals(2, dbGateway.getAllCalls)
    }

    @Test
    fun `load error is routed to the error handler and notifies`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        dbGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("boom", errorHandler.proceededErrors.single().message)
        assertEquals(listOf("boom"), alerts.map { it.text })
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(emptyList(), viewModel.uiState.value.data)
    }

    @Test
    fun `add error keeps the input and reports both failures`() = runTest(rule.dispatcher) {
        // Arrange
        val alerts = collectAlerts()
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()
        viewModel.onNameInput("hello")
        dbGateway.errorToThrow = IllegalStateException("boom")

        // Act
        viewModel.addData()
        advanceUntilIdle()

        // Assert
        assertEquals("hello", viewModel.nameInput)
        assertEquals(emptyList(), dbGateway.addedData)
        assertEquals(2, errorHandler.proceededErrors.size)
        assertEquals(listOf("boom", "boom"), alerts.map { it.text })
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `delete error is routed to the error handler and clears progress`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = DBExampleViewModel()
        advanceUntilIdle()
        dbGateway.errorToThrow = IllegalStateException("boom")

        // Act
        viewModel.deleteData(DBExampleData(id = 1, name = "row"))
        advanceUntilIdle()

        // Assert
        assertEquals(2, errorHandler.proceededErrors.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
