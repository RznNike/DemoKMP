package ru.rznnike.demokmp.app.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.rznnike.demokmp.domain.model.common.Language
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * API 30 pins these tests to `AppCompatDelegate`'s pre-33 in-process locale store. On 33+ the same
 * calls are forwarded to the framework `LocaleManager`, which is a different code path and not what
 * `ActivityUtils` does on the majority of supported devices (minSdk 26).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], application = Application::class)
class ActivityUtilsTest {
    @BeforeTest
    fun setUp() = AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())

    @AfterTest
    fun tearDown() = AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())

    @Test
    fun `setSelectedLanguage then getSelectedLanguage round-trips Russian`() {
        // Act
        setSelectedLanguage(Language.RU)

        // Assert
        assertEquals(Language.RU, getSelectedLanguage())
    }

    @Test
    fun `setSelectedLanguage then getSelectedLanguage round-trips English`() {
        // Act
        setSelectedLanguage(Language.EN)

        // Assert
        assertEquals(Language.EN, getSelectedLanguage())
    }

    @Test
    fun `getSelectedLanguage falls back to the default language for an unsupported device locale`() {
        // Arrange
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("de-DE"))

            // Act
            val language = getSelectedLanguage()

            // Assert
            assertEquals(Language.default, language)
        } finally {
            Locale.setDefault(original)
        }
    }
}
