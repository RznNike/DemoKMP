package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.set
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferenceTest {
    @Test
    fun `get returns default value when key is missing`() {
        // Arrange
        val preference = Preference.StringPreference(MapSettings(), "key", "fallback")

        // Act
        val result = preference.get()

        // Assert
        assertEquals("fallback", result)
    }

    @Test
    fun `set then get round-trips value`() {
        // Arrange
        val preference = Preference.StringPreference(MapSettings(), "key")

        // Act
        preference.set("value")

        // Assert
        assertEquals("value", preference.get())
    }

    @Test
    fun `set stores value under its own key`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.StringPreference(settings, "key")

        // Act
        preference.set("value")

        // Assert
        assertEquals("value", settings.getStringOrNull("key"))
    }

    @Test
    fun `preferences with different keys are independent`() {
        // Arrange
        val settings = MapSettings()
        val first = Preference.StringPreference(settings, "first", "first-default")
        val second = Preference.StringPreference(settings, "second", "second-default")

        // Act
        first.set("changed")

        // Assert
        assertEquals("second-default", second.get())
    }

    @Test
    fun `get returns default after stored value is removed`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.IntPreference(settings, "key", 7)
        preference.set(42)
        settings.remove("key")

        // Act
        val result = preference.get()

        // Assert
        assertEquals(7, result)
    }

    @Test
    fun `string preference keeps raw text`() {
        // Arrange
        val preference = Preference.StringPreference(MapSettings(), "key")

        // Act
        preference.set(" Hello, мир ")

        // Assert
        assertEquals(" Hello, мир ", preference.get())
    }

    @Test
    fun `int preference round-trips negative value`() {
        // Arrange
        val preference = Preference.IntPreference(MapSettings(), "key")

        // Act
        preference.set(-42)

        // Assert
        assertEquals(-42, preference.get())
    }

    @Test
    fun `int preference is stored as decimal string`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.IntPreference(settings, "key")

        // Act
        preference.set(42)

        // Assert
        assertEquals("42", settings.getStringOrNull("key"))
    }

    @Test
    fun `int preference returns default for corrupt stored value`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "not-a-number"
        val preference = Preference.IntPreference(settings, "key", 7)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(7, result)
    }

    @Test
    fun `long preference round-trips value beyond int range`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.LongPreference(settings, "key")

        // Act
        preference.set(9_000_000_000L)

        // Assert
        assertEquals(9_000_000_000L, preference.get())
        assertEquals("9000000000", settings.getStringOrNull("key"))
    }

    @Test
    fun `long preference returns default for corrupt stored value`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "1.5"
        val preference = Preference.LongPreference(settings, "key", 3L)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(3L, result)
    }

    @Test
    fun `double preference round-trips fractional value`() {
        // Arrange
        val setting = Preference.DoublePreference(MapSettings(), "key")

        // Act
        setting.set(1.25)

        // Assert
        assertEquals(1.25, setting.get())
    }

    @Test
    fun `double preference accepts comma as decimal separator`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "1,5"
        val preference = Preference.DoublePreference(settings, "key")

        // Act
        val result = preference.get()

        // Assert
        assertEquals(1.5, result)
    }

    @Test
    fun `double preference returns default for corrupt stored value`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "abc"
        val preference = Preference.DoublePreference(settings, "key", 2.5)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(2.5, result)
    }

    @Test
    fun `boolean preference round-trips both values`() {
        // Arrange
        val preference = Preference.BooleanPreference(MapSettings(), "key")

        // Act + Assert
        listOf(true, false).forEach { value ->
            preference.set(value)
            assertEquals(value, preference.get(), "value=$value")
        }
    }

    @Test
    fun `boolean preference is stored as lowercase text`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.BooleanPreference(settings, "key")

        // Act
        preference.set(true)

        // Assert
        assertEquals("true", settings.getStringOrNull("key"))
    }

    @Test
    fun `boolean preference returns default for non strict stored text`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "yes"
        val preference = Preference.BooleanPreference(settings, "key", true)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(true, result)
    }

    @Test
    fun `language preference round-trips value`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.LanguagePreference(settings, "key", Language.EN)

        // Act
        preference.set(Language.RU)

        // Assert
        assertEquals(Language.RU, preference.get())
        assertEquals(Language.RU.fullTag, settings.getStringOrNull("key"))
    }

    @Test
    fun `language preference returns configured default when key is missing`() {
        // Arrange
        val preference = Preference.LanguagePreference(MapSettings(), "key", Language.RU)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(Language.RU, result)
    }

    @Test
    fun `language preference falls back to enum default for unknown tag`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "xx-XX"
        val preference = Preference.LanguagePreference(settings, "key", Language.RU)

        // Act
        val result = preference.get()

        // Assert
        // deserialization delegates to Language[tag], which yields Language.default
        assertEquals(Language.default, result)
    }

    @Test
    fun `theme preference round-trips value`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.ThemePreference(settings, "key", Theme.AUTO)

        // Act
        preference.set(Theme.DARK_CONTRAST)

        // Assert
        assertEquals(Theme.DARK_CONTRAST, preference.get())
        assertEquals(Theme.DARK_CONTRAST.id.toString(), settings.getStringOrNull("key"))
    }

    @Test
    fun `theme preference returns configured default when key is missing`() {
        // Arrange
        val preference = Preference.ThemePreference(MapSettings(), "key", Theme.DARK)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(Theme.DARK, result)
    }

    @Test
    fun `theme preference falls back to enum default for unknown id`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "99"
        val preference = Preference.ThemePreference(settings, "key", Theme.DARK)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(Theme.default, result)
    }

    @Test
    fun `two sided print preference round-trips value`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.TwoSidedPrintPreference(settings, "key", TwoSidedPrint.DISABLED)

        // Act
        preference.set(TwoSidedPrint.TWO_SIDED_SHORT_EDGE)

        // Assert
        assertEquals(TwoSidedPrint.TWO_SIDED_SHORT_EDGE, preference.get())
        assertEquals(TwoSidedPrint.TWO_SIDED_SHORT_EDGE.id.toString(), settings.getStringOrNull("key"))
    }

    @Test
    fun `two sided print preference falls back to enum default for non numeric value`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "abc"
        val preference = Preference.TwoSidedPrintPreference(settings, "key", TwoSidedPrint.TWO_SIDED_LONG_EDGE)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(TwoSidedPrint.default, result)
    }

    @Test
    fun `ui scale preference round-trips value`() {
        // Arrange
        val settings = MapSettings()
        val preference = Preference.UiScalePreference(settings, "key", UiScale.S100)

        // Act
        preference.set(UiScale.S130)

        // Assert
        assertEquals(UiScale.S130, preference.get())
        assertEquals(UiScale.S130.value.toString(), settings.getStringOrNull("key"))
    }

    @Test
    fun `ui scale preference falls back to enum default for unknown value`() {
        // Arrange
        val settings = MapSettings()
        settings["key"] = "0"
        val preference = Preference.UiScalePreference(settings, "key", UiScale.S130)

        // Act
        val result = preference.get()

        // Assert
        assertEquals(UiScale.default, result)
    }
}
