package ru.rznnike.demokmp.app.utils

import android.app.Application
import android.content.Context
import org.junit.runner.RunWith
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The Android actuals are thin: a version string, a `null` MAC address (the platform forbids
 * reading it), no COM library, and a Koin-provided `Context` for link opening. Robolectric is
 * needed for [android.os.Build.VERSION.RELEASE] and for recording the launched intent.
 *
 * `openLink` resolves its `Context` through `KoinComponent.getKoin()`, which a private test
 * implementation overrides — no global Koin context is started.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
class PlatformUtilsAndroidTest {
    private class TestKoinComponent(private val koin: Koin) : KoinComponent {
        override fun getKoin(): Koin = koin
    }

    @Test
    fun `platformName reports the Android release`() {
        assertEquals("Android ${android.os.Build.VERSION.RELEASE}", platformName)
    }

    @Test
    fun `getMacAddress is unavailable on Android`() {
        assertNull(getMacAddress())
    }

    @Test
    fun `COM library lifecycle is a no-op on Android`() {
        // Arrange
        val result = runCatching {
            initCOMLibrary()
            destroyCOMLibrary()
        }

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `KoinComponent openLink starts a new-task VIEW intent`() {
        // Arrange
        val application = RuntimeEnvironment.getApplication()
        val koin = koinApplication {
            modules(module { single<Context> { application } })
        }

        // Act
        try {
            TestKoinComponent(koin.koin).openLink("https://example.com/")
        } finally {
            koin.close()
        }

        // Assert
        val intent = shadowOf(application).nextStartedActivity
        assertEquals("https://example.com/", intent.data.toString())
    }
}
