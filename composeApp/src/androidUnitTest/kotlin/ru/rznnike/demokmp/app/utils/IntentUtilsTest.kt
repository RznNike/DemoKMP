package ru.rznnike.demokmp.app.utils

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import ru.rznnike.demokmp.domain.model.common.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Covers the intent this module actually launches, plus the two settings fallbacks.
 *
 * The `openNavigation` / `openGooglePlayPage` `catch (ActivityNotFoundException)` blocks are
 * intentionally not exercised: both delegate to `Context.openLink`, which catches every `Exception`
 * itself, so those blocks are unreachable.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
class IntentUtilsTest {
    private val application: Application = RuntimeEnvironment.getApplication()

    /**
     * Throws [ActivityNotFoundException] for the first `startActivity` call, then records every
     * subsequent intent — the shape needed to observe a `catch → retry` fallback.
     */
    private class FailFirstStartContext(base: Context) : ContextWrapper(base) {
        private var failuresRemaining = 1
        val startedIntents = mutableListOf<Intent>()

        override fun startActivity(intent: Intent) {
            if (failuresRemaining > 0) {
                failuresRemaining--
                throw ActivityNotFoundException("no activity handles $intent")
            }
            startedIntents += intent
        }
    }

    @Test
    fun `openLink puts the url on a VIEW intent`() {
        // Act
        application.openLink("https://example.com/path")

        // Assert
        assertEquals(
            "https://example.com/path",
            shadowOf(application).nextStartedActivity.data.toString()
        )
    }

    @Test
    fun `openLink launches the intent in a new task`() {
        // Act
        application.openLink("https://example.com/")

        // Assert
        val flags = shadowOf(application).nextStartedActivity.flags
        assertTrue(flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun `openLink reports the failure to the error callback`() {
        // Arrange
        val context = FailFirstStartContext(application)
        var reported: Exception? = null

        // Act
        context.openLink(link = "https://example.com/", onErrorCallback = { reported = it })

        // Assert
        assertTrue(reported is ActivityNotFoundException)
    }

    @Test
    fun `openDial puts the phone number on a DIAL intent`() {
        // Act
        application.openDial("79991234567")

        // Assert
        assertEquals("tel:79991234567", shadowOf(application).nextStartedActivity.data.toString())
    }

    @Test
    fun `openAppSettings falls back to the general settings screen`() {
        // Arrange
        val context = FailFirstStartContext(application)

        // Act
        context.openAppSettings()

        // Assert
        assertEquals(Settings.ACTION_SETTINGS, context.startedIntents.single().action)
    }

    @Test
    fun `openNotificationSettings falls back to the general settings screen`() {
        // Arrange
        val context = FailFirstStartContext(application)

        // Act
        context.openNotificationSettings()

        // Assert
        assertEquals(Settings.ACTION_SETTINGS, context.startedIntents.single().action)
    }

    @Test
    fun `openGooglePlayPage targets the Play Store app`() {
        // Act
        application.openGooglePlayPage()

        // Assert
        assertEquals("com.android.vending", shadowOf(application).nextStartedActivity.`package`)
    }

    @Test
    fun `openNavigation with a position embeds the point twice in the geo uri`() {
        // Act
        application.openNavigation(position = Position(lat = 55.75, lng = 37.62))

        // Assert
        assertEquals(
            "geo:55.750000,37.620000?q=55.750000,37.620000",
            shadowOf(application).nextStartedActivity.data.toString()
        )
    }

    @Test
    fun `openNavigation without a position searches by address`() {
        // Act
        application.openNavigation(address = "Moscow")

        // Assert
        assertEquals("geo:0,0?q=Moscow", shadowOf(application).nextStartedActivity.data.toString())
    }
}
