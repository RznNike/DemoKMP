package ru.rznnike.demokmp.domain.interactor.chartexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeChartExampleGateway
import ru.rznnike.demokmp.domain.model.chart.ChartPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetChartSampleDataUseCaseTest {
    @Test
    fun `returns the sample points provided by the gateway`() = runTest {
        // Arrange
        val expected = listOf(ChartPoint(x = 1.0, y = 2.0), ChartPoint(x = 3.0, y = 4.0))
        val gateway = FakeChartExampleGateway().apply { sampleData = expected }
        val useCase = GetChartSampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeChartExampleGateway().apply { errorToThrow = expected }
        val useCase = GetChartSampleDataUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
