package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.chart.ChartPoint

interface ChartExampleGateway {
    suspend fun getSampleData(): List<ChartPoint>
}