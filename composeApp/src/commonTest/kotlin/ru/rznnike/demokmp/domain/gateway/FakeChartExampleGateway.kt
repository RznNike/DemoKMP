package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.chart.ChartPoint

/**
 * Test double for [ChartExampleGateway].
 *
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeChartExampleGateway : ChartExampleGateway {
    var errorToThrow: Exception? = null

    var sampleData: List<ChartPoint> = emptyList()

    override suspend fun getSampleData(): List<ChartPoint> {
        errorToThrow?.let { throw it }
        return sampleData
    }
}
