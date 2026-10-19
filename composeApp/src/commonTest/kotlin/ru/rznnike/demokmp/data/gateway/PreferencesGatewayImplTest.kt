package ru.rznnike.demokmp.data.gateway

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesGatewayImplTest {
    private fun createGateway(settings: Settings = MapSettings()): PreferencesGateway =
        PreferencesGatewayImpl(PreferencesManager(settings))

    @Test
    fun `getters return defaults when nothing is stored`() = runTest {
        // Arrange
        val gateway = createGateway()

        // Assert
        assertEquals(0, gateway.getTestCounter())
        assertEquals(Language.EN, gateway.getLanguage())
        assertEquals(Theme.AUTO, gateway.getTheme())
        assertEquals(UiScale.S100, gateway.getUiScale())
        assertEquals(PrintSettings(), gateway.getPrintSettings())
    }

    @Test
    fun `test counter round-trips`() = runTest {
        // Arrange
        val gateway = createGateway()

        // Act
        gateway.setTestCounter(42)

        // Assert
        assertEquals(42, gateway.getTestCounter())
    }

    @Test
    fun `language round-trips`() = runTest {
        // Arrange
        val settings = MapSettings()
        val gateway = createGateway(settings)

        // Act
        gateway.setLanguage(Language.RU)

        // Assert
        assertEquals(Language.RU, gateway.getLanguage())
        // persisted as the full tag (cross-platform storage contract)
        assertEquals(Language.RU.fullTag, settings.getStringOrNull("language"))
    }

    @Test
    fun `theme round-trips`() = runTest {
        // Arrange
        val settings = MapSettings()
        val gateway = createGateway(settings)

        // Act
        gateway.setTheme(Theme.DARK)

        // Assert
        assertEquals(Theme.DARK, gateway.getTheme())
        // persisted as the theme id (cross-platform storage contract)
        assertEquals(Theme.DARK.id.toString(), settings.getStringOrNull("theme"))
    }

    @Test
    fun `ui scale round-trips`() = runTest {
        // Arrange
        val gateway = createGateway()

        // Act
        gateway.setUiScale(UiScale.S130)

        // Assert
        assertEquals(UiScale.S130, gateway.getUiScale())
    }

    @Test
    fun `print settings round-trip both fields`() = runTest {
        // Arrange
        val gateway = createGateway()
        val printSettings = PrintSettings(
            printerName = "HP LaserJet",
            twoSidedPrint = TwoSidedPrint.TWO_SIDED_LONG_EDGE
        )

        // Act
        gateway.setPrintSettings(printSettings)

        // Assert
        assertEquals(printSettings, gateway.getPrintSettings())
    }

    @Test
    fun `print settings read fields independently`() = runTest {
        // Arrange: only the printer name was ever stored
        val settings = MapSettings()
        settings["printerName"] = "Some Printer"
        val gateway = createGateway(settings)

        // Act
        val result = gateway.getPrintSettings()

        // Assert
        assertEquals("Some Printer", result.printerName)
        assertEquals(TwoSidedPrint.DISABLED, result.twoSidedPrint)
    }

    @Test
    fun `values persist across gateway instances`() = runTest {
        // Arrange
        val settings = MapSettings()
        val writer = createGateway(settings)
        writer.setTestCounter(7)
        writer.setLanguage(Language.RU)
        writer.setPrintSettings(
            PrintSettings(
                printerName = "Printer",
                twoSidedPrint = TwoSidedPrint.TWO_SIDED_SHORT_EDGE
            )
        )

        // Act
        val reader = createGateway(settings)

        // Assert
        assertEquals(7, reader.getTestCounter())
        assertEquals(Language.RU, reader.getLanguage())
        assertEquals(
            PrintSettings("Printer", TwoSidedPrint.TWO_SIDED_SHORT_EDGE),
            reader.getPrintSettings()
        )
    }

    @Test
    fun `unknown stored values fall back to defaults`() = runTest {
        // Arrange
        val settings = MapSettings()
        settings["testCounter"] = "not-a-number"
        settings["language"] = "xx-XX"
        settings["theme"] = "99"
        settings["uiScale"] = "0"
        settings["twoSidedPrint"] = "99"
        val gateway = createGateway(settings)

        // Assert
        assertEquals(0, gateway.getTestCounter())
        assertEquals(Language.EN, gateway.getLanguage())
        assertEquals(Theme.AUTO, gateway.getTheme())
        assertEquals(UiScale.S100, gateway.getUiScale())
        assertEquals(PrintSettings(), gateway.getPrintSettings())
    }

    @Test
    fun `preferences are independent`() = runTest {
        // Arrange
        val gateway = createGateway()

        // Act
        gateway.setTestCounter(5)
        gateway.setTheme(Theme.DARK_CONTRAST)

        // Assert
        assertEquals(5, gateway.getTestCounter())
        assertEquals(Theme.DARK_CONTRAST, gateway.getTheme())
        assertEquals(Language.EN, gateway.getLanguage())
        assertEquals(UiScale.S100, gateway.getUiScale())
        assertEquals(PrintSettings(), gateway.getPrintSettings())
    }
}
