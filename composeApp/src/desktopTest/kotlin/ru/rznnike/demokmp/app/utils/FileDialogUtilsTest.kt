package ru.rznnike.demokmp.app.utils

import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import javax.print.attribute.standard.Sides
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the print-setting mapping extracted from `printDialog`
 */
class FileDialogUtilsTest {
    @Test
    fun `DISABLED maps to one sided printing`() {
        // Act
        val result = TwoSidedPrint.DISABLED.toSides()

        // Assert
        assertEquals(Sides.ONE_SIDED, result)
    }

    @Test
    fun `TWO_SIDED_LONG_EDGE maps to two sided long edge printing`() {
        // Act
        val result = TwoSidedPrint.TWO_SIDED_LONG_EDGE.toSides()

        // Assert
        assertEquals(Sides.TWO_SIDED_LONG_EDGE, result)
    }

    @Test
    fun `TWO_SIDED_SHORT_EDGE maps to two sided short edge printing`() {
        // Act
        val result = TwoSidedPrint.TWO_SIDED_SHORT_EDGE.toSides()

        // Assert
        assertEquals(Sides.TWO_SIDED_SHORT_EDGE, result)
    }
}
