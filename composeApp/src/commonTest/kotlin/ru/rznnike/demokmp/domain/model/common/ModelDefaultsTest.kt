package ru.rznnike.demokmp.domain.model.common

import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelDefaultsTest {
    @Test
    fun `Language get maps full tags`() {
        // Act
        val ru = Language["ru-RU"]
        val en = Language["en-US"]

        // Assert
        assertEquals(Language.RU, ru)
        assertEquals(Language.EN, en)
    }

    @Test
    fun `Language get falls back to default for an unknown full tag`() {
        // Act
        val result = Language["de-DE"]

        // Assert
        assertEquals(Language.EN, result)
    }

    @Test
    fun `Language getByShortTag maps short tags`() {
        // Act
        val ru = Language.getByShortTag("ru")
        val en = Language.getByShortTag("en")

        // Assert
        assertEquals(Language.RU, ru)
        assertEquals(Language.EN, en)
    }

    @Test
    fun `Language getByShortTag falls back to default for an unknown short tag`() {
        // Act
        val result = Language.getByShortTag("de")

        // Assert
        assertEquals(Language.EN, result)
    }

    @Test
    fun `Theme get maps ids`() {
        // Act
        val auto = Theme[0]
        val light = Theme[1]
        val darkContrast = Theme[4]

        // Assert
        assertEquals(Theme.AUTO, auto)
        assertEquals(Theme.LIGHT, light)
        assertEquals(Theme.DARK_CONTRAST, darkContrast)
    }

    @Test
    fun `Theme get falls back to AUTO for an unknown id`() {
        // Act
        val result = Theme[99]

        // Assert
        assertEquals(Theme.AUTO, result)
    }

    @Test
    fun `UiScale get maps values`() {
        // Act
        val s100 = UiScale[100]
        val s130 = UiScale[130]

        // Assert
        assertEquals(UiScale.S100, s100)
        assertEquals(UiScale.S130, s130)
    }

    @Test
    fun `UiScale get falls back to S100 for an unknown value`() {
        // Act
        val result = UiScale[75]

        // Assert
        assertEquals(UiScale.S100, result)
    }

    @Test
    fun `TwoSidedPrint get maps ids`() {
        // Act
        val disabled = TwoSidedPrint[0]
        val shortEdge = TwoSidedPrint[2]

        // Assert
        assertEquals(TwoSidedPrint.DISABLED, disabled)
        assertEquals(TwoSidedPrint.TWO_SIDED_SHORT_EDGE, shortEdge)
    }

    @Test
    fun `TwoSidedPrint get falls back to DISABLED for null`() {
        // Act
        val result = TwoSidedPrint[null]

        // Assert
        assertEquals(TwoSidedPrint.DISABLED, result)
    }

    @Test
    fun `TwoSidedPrint get falls back to DISABLED for an unknown id`() {
        // Act
        val result = TwoSidedPrint[99]

        // Assert
        assertEquals(TwoSidedPrint.DISABLED, result)
    }
}
