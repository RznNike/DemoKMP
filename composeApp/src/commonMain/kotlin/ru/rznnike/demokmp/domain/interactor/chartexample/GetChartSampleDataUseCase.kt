package ru.rznnike.demokmp.domain.interactor.chartexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.model.chart.ChartPoint

class GetChartSampleDataUseCase(
    private val chartExampleGateway: ChartExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<List<ChartPoint>>(dispatcherProvider) {
    override suspend fun execute() = chartExampleGateway.getSampleData()
}