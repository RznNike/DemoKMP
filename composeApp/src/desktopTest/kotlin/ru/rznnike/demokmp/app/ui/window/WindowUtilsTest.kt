package ru.rznnike.demokmp.app.ui.window

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Dimension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Covers the pure DPI math behind `FrameWindowScope.SetMinimumSize`.
 *
 * The unspecified cases assert the `null` contract: an unspecified `Dp` converts to `NaN`, which
 * `roundToInt()` rejects with `IllegalArgumentException`, so the function must bail out before the
 * conversion instead of propagating the crash into window setup.
 */
class WindowUtilsTest {
    private val unitDensity = Density(1f)

    @Test
    fun `scale 100 keeps the size unchanged at density 1`() {
        // Act
        val result = calculateMinimumSize(
            width = 400.dp,
            height = 300.dp,
            scale = 100,
            density = unitDensity
        )

        // Assert
        assertEquals(Dimension(400, 300), result)
    }

    @Test
    fun `scale 150 multiplies the size by one and a half`() {
        // Act
        val result = calculateMinimumSize(
            width = 400.dp,
            height = 300.dp,
            scale = 150,
            density = unitDensity
        )

        // Assert
        assertEquals(Dimension(600, 450), result)
    }

    @Test
    fun `scale 200 doubles the size`() {
        // Act
        val result = calculateMinimumSize(
            width = 400.dp,
            height = 300.dp,
            scale = 200,
            density = unitDensity
        )

        // Assert
        assertEquals(Dimension(800, 600), result)
    }

    @Test
    fun `the density conversion is applied before the scale`() {
        // Act
        val result = calculateMinimumSize(
            width = 200.dp,
            height = 100.dp,
            scale = 100,
            density = Density(2f)
        )

        // Assert
        assertEquals(Dimension(400, 200), result)
    }

    @Test
    fun `a fractional scale is applied to the converted pixels`() {
        // Act
        val result = calculateMinimumSize(
            width = 200.dp,
            height = 100.dp,
            scale = 50,
            density = Density(2f)
        )

        // Assert
        assertEquals(Dimension(200, 100), result)
    }

    @Test
    fun `a fractional pixel result is rounded to the nearest integer`() {
        // Act: 101.dp * 1.5 = 151.5 px, 50.dp * 1.5 = 75.0 px
        val result = calculateMinimumSize(
            width = 101.dp,
            height = 50.dp,
            scale = 100,
            density = Density(1.5f)
        )

        // Assert
        assertEquals(Dimension(152, 75), result)
    }

    @Test
    fun `a fully unspecified size yields no minimum size`() {
        // Act
        val result = calculateMinimumSize(
            width = Dp.Unspecified,
            height = Dp.Unspecified,
            scale = 100,
            density = unitDensity
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `a partially unspecified size yields no minimum size`() {
        // Act
        val unspecifiedWidth = calculateMinimumSize(
            width = Dp.Unspecified,
            height = 300.dp,
            scale = 100,
            density = unitDensity
        )
        val unspecifiedHeight = calculateMinimumSize(
            width = 400.dp,
            height = Dp.Unspecified,
            scale = 100,
            density = unitDensity
        )

        // Assert
        assertNull(unspecifiedWidth)
        assertNull(unspecifiedHeight)
    }
}
