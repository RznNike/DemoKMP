package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartExampleGatewayImplTest {
    private val gateway: ChartExampleGateway = ChartExampleGatewayImpl()

    @Test
    fun `returns three hundred and one sample points`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        assertEquals(301, result.size)
    }

    @Test
    fun `x coordinate equals index divided by ten`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        result.forEachIndexed { index, point ->
            assertEquals(index.toDouble() / 10, point.x, "point[$index].x")
        }
    }

    @Test
    fun `y coordinate equals sine of x`() = runTest {
        // Act
        val result = gateway.getSampleData()

        // Assert
        result.forEachIndexed { index, point ->
            assertEquals(sin(point.x), point.y, absoluteTolerance = 1e-12, message = "point[$index].y")
        }
    }

    @Test
    fun `returns equal data on repeated calls`() = runTest {
        // Act
        val firstResult = gateway.getSampleData()
        val secondResult = gateway.getSampleData()

        // Assert
        assertEquals(firstResult, secondResult)
    }
}
