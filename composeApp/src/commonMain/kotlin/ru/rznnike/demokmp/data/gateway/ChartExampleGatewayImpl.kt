package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.model.chart.ChartPoint
import kotlin.math.sin

class ChartExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider
) : ChartExampleGateway {
    override suspend fun getSampleData(): List<ChartPoint> = withContext(dispatcherProvider.io) {
        (0..300).map { index ->
            val x = index.toDouble() / 10
            ChartPoint(
                x = x,
                y = sin(x)
            )
        }
    }
}