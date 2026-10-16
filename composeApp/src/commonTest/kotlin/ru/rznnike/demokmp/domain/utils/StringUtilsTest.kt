package ru.rznnike.demokmp.domain.utils

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringUtilsTest {
    @Test
    fun `toBigDecimalOrNullSmart parses plain number`() {
        // Act
        val result = "123.45".toBigDecimalOrNullSmart()

        // Assert
        assertEquals(BigDecimal("123.45"), result)
    }

    @Test
    fun `toBigDecimalOrNullSmart ignores grouping spaces`() {
        // Act
        val result = "1 234 567.89".toBigDecimalOrNullSmart()

        // Assert
        assertEquals(BigDecimal("1234567.89"), result)
    }

    @Test
    fun `toBigDecimalOrNullSmart accepts comma as decimal separator`() {
        // Act
        val result = "1234,56".toBigDecimalOrNullSmart()

        // Assert
        assertEquals(BigDecimal("1234.56"), result)
    }

    @Test
    fun `toBigDecimalOrNullSmart returns null for non-numeric input`() {
        // Act
        val result = "abc".toBigDecimalOrNullSmart()

        // Assert
        assertNull(result)
    }

    @Test
    fun `toBigDecimalOrNullSmart returns null for empty input`() {
        // Act
        val result = "".toBigDecimalOrNullSmart()

        // Assert
        assertNull(result)
    }

    @Test
    fun `toBigDecimalSmart falls back to zero for non-numeric input`() {
        // Act
        val result = "abc".toBigDecimalSmart()

        // Assert
        assertEquals(BigDecimal.ZERO, result)
    }

    @Test
    fun `toBigDecimalSmart parses valid input`() {
        // Act
        val result = "1 000,5".toBigDecimalSmart()

        // Assert
        assertEquals(BigDecimal("1000.5"), result)
    }

    @Test
    fun `toDoubleOrNullSmart parses comma separated and grouped input`() {
        // Act
        val result = "1 234,5".toDoubleOrNullSmart()

        // Assert
        assertEquals(1234.5, result)
    }

    @Test
    fun `toDoubleOrNullSmart returns null for non-numeric input`() {
        // Act
        val result = "abc".toDoubleOrNullSmart()

        // Assert
        assertNull(result)
    }

    @Test
    fun `getNumberFormat forces requested precision`() {
        // Act
        val result = getNumberFormat(precision = 2).format(1234.5)

        // Assert
        assertEquals("1 234.50", result)
    }

    @Test
    fun `getNumberFormat keeps optional precision`() {
        // Act
        val result = getNumberFormat(precision = 0).format(1.5)

        // Assert
        assertEquals("1.5", result)
    }

    @Test
    fun `getNumberFormat omits grouping when requested`() {
        // Act
        val result = getNumberFormat(precision = 2, grouping = false).format(1234.5)

        // Assert
        assertEquals("1234.50", result)
    }

    @Test
    fun `moneyDecimal2Format groups and forces two decimals`() {
        // Act
        val result = moneyDecimal2Format.format(1234.5)

        // Assert
        assertEquals("1 234.50", result)
    }

    @Test
    fun `decimal2Format drops trailing zeros`() {
        // Act
        val result = decimal2Format.format(1.0)

        // Assert
        assertEquals("1", result)
    }

    @Test
    fun `decimal2Format keeps significant decimals`() {
        // Act
        val result = decimal2Format.format(1.5)

        // Assert
        assertEquals("1.5", result)
    }
}
