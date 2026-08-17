package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.model.chart.ChartPoint
import kotlin.math.sin

class ChartExampleGatewayImpl : ChartExampleGateway {
    override suspend fun getSampleData(): List<ChartPoint> {
        return (0..300).map { index ->
            val x = index.toDouble() / 10
            ChartPoint(
                x = x,
                y = sin(x)
            )
        }
    }
}