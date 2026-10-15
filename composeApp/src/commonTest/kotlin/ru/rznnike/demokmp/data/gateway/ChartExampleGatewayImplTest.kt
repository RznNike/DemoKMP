package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.model.chart.ChartPoint
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartExampleGatewayImplTest {
    private val gateway: ChartExampleGateway = ChartExampleGatewayImpl()

    @Test
    fun `getSampleData returns 301 points`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        assertEquals(301, result.size)
    }

    @Test
    fun `getSampleData starts at point with x and y equal to 0`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        assertEquals(ChartPoint(x = 0.0, y = 0.0), result.first())
    }

    @Test
    fun `getSampleData ends at point with x equal to 30`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        assertEquals(ChartPoint(x = 30.0, y = sin(30.0)), result.last())
    }

    @Test
    fun `getSampleData x values equal index divided by 10`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        result.forEachIndexed { index, point ->
            assertEquals(index.toDouble() / 10, point.x, "point[$index].x")
        }
    }

    @Test
    fun `getSampleData y values equal sin of x within tolerance`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        result.forEachIndexed { index, point ->
            assertEquals(sin(point.x), point.y, absoluteTolerance = 1e-12, message = "point[$index].y")
        }
    }

    @Test
    fun `getSampleData returns equal data on repeated calls`() = runTest {
        // Act
        val firstResult = gateway.getSampleData()
        val secondResult = gateway.getSampleData()

        // Assert
        assertEquals(firstResult, secondResult)
    }
}
