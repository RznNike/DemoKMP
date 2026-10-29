package ru.rznnike.demokmp.app.viewmodel.chartexample

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.app.error.FakeErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.gateway.FakeChartExampleGateway
import ru.rznnike.demokmp.domain.interactor.chartexample.GetChartSampleDataUseCase
import ru.rznnike.demokmp.domain.model.chart.ChartPoint

@OptIn(ExperimentalCoroutinesApi::class)
class ChartExampleViewModelTest {
    private val rule = ViewModelTestRule()
    private lateinit var chartGateway: FakeChartExampleGateway
    private lateinit var errorHandler: FakeErrorHandler

    @BeforeTest
    fun setUp() {
        rule.start()
        chartGateway = FakeChartExampleGateway()
        errorHandler = FakeErrorHandler()
        startKoin {
            modules(
                module {
                    single<DispatcherProvider> { rule.dispatcherProvider }
                    single<CoroutineScopeProvider> { rule.coroutineScopeProvider }
                    single<ErrorHandler> { errorHandler }
                    single { Notifier(get()) }
                    single<ChartExampleGateway> { chartGateway }
                    single { GetChartSampleDataUseCase(get(), get()) }
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
    fun `sample data is loaded into the state on init`() = runTest(rule.dispatcher) {
        // Arrange
        val expected = listOf(
            ChartPoint(x = 1.0, y = 10.0),
            ChartPoint(x = 2.0, y = 20.0)
        )
        chartGateway.sampleData = expected

        // Act
        val viewModel = ChartExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(expected, viewModel.uiState.value.data)
    }

    @Test
    fun `load error is routed to the error handler and leaves the data empty`() = runTest(rule.dispatcher) {
        // Arrange
        chartGateway.errorToThrow = IllegalStateException("boom")

        // Act
        val viewModel = ChartExampleViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("boom", errorHandler.proceededErrors.single().message)
        assertEquals(emptyList(), viewModel.uiState.value.data)
    }
}
