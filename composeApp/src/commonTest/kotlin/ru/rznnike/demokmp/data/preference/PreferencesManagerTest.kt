package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.set
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesManagerTest {
    @Test
    fun `preferences are persisted under the agreed storage keys`() {
        // Arrange
        val settings = MapSettings()
        val manager = PreferencesManager(settings)

        // Act
        manager.testCounter.set(42)
        manager.language.set(Language.RU)
        manager.theme.set(Theme.DARK)
        manager.printerName.set("HP LaserJet")
        manager.twoSidedPrint.set(TwoSidedPrint.TWO_SIDED_LONG_EDGE)
        manager.uiScale.set(UiScale.S130)

        // Assert
        assertEquals(
            setOf("testCounter", "language", "theme", "printerName", "twoSidedPrint", "uiScale"),
            settings.keys
        )
    }

    @Test
    fun `clearAll resets every preference to its default`() {
        // Arrange
        val manager = PreferencesManager(MapSettings())
        manager.testCounter.set(42)
        manager.language.set(Language.RU)
        manager.theme.set(Theme.DARK)
        manager.printerName.set("HP LaserJet")
        manager.twoSidedPrint.set(TwoSidedPrint.TWO_SIDED_LONG_EDGE)
        manager.uiScale.set(UiScale.S130)

        // Act
        manager.clearAll()

        // Assert
        assertEquals(0, manager.testCounter.get())
        assertEquals(Language.default, manager.language.get())
        assertEquals(Theme.default, manager.theme.get())
        assertEquals("", manager.printerName.get())
        assertEquals(TwoSidedPrint.default, manager.twoSidedPrint.get())
        assertEquals(UiScale.default, manager.uiScale.get())
    }

    @Test
    fun `clearAll on empty settings does not throw`() {
        // Arrange
        val settings = MapSettings()
        val manager = PreferencesManager(settings)

        // Act
        manager.clearAll()

        // Assert
        assertEquals(emptySet(), settings.keys)
    }

    @Test
    fun `clearAll removes keys that are not preferences`() {
        // Arrange: clearAll() delegates to Settings.clear(), so it is a blanket wipe
        val settings = MapSettings()
        settings["unrelatedKey"] = "value"
        val manager = PreferencesManager(settings)

        // Act
        manager.clearAll()

        // Assert
        assertEquals(emptySet(), settings.keys)
    }
}
