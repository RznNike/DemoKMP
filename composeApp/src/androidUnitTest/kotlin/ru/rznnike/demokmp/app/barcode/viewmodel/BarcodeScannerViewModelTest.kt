package ru.rznnike.demokmp.app.barcode.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The ViewModel is constructed with CameraX still unbound: `unbindCallback` defaults to `{}` and
 * `surfaceMeteringPointFactory` stays `null` until `setSurfaceProvider` fires. Both public entry
 * points must therefore be harmless before `bindToCamera` runs — a screen that is disposed or
 * tapped before the preview arrives must not crash.
 *
 * `Preview.Builder().build()` runs in the constructor, but `bindToCamera` (the only code touching
 * the camera service) is never called, so no device is required.
 *
 * Koin supplies `java.time.Clock` for `BarcodeAnalyzer`; it is only resolved on bind, and starting
 * it here keeps the test honest about the ViewModel's declared dependencies.
 *
 * `ExperimentalCamera2Interop` is a plain annotation (not `@RequiresOptIn`), so no opt-in is needed.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
class BarcodeScannerViewModelTest {
    @BeforeTest
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<Clock> { Clock.systemUTC() }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun `unbind before bind completes without a camera attached`() {
        // Arrange
        val viewModel = BarcodeScannerViewModel(formats = emptyList())

        // Act
        val result = runCatching { viewModel.unbind() }

        // Assert
        assertTrue(result.isSuccess, "the default unbind callback must be a no-op")
    }

    @Test
    fun `unbind before bind leaves the surface request unset`() {
        // Arrange
        val viewModel = BarcodeScannerViewModel(formats = emptyList())

        // Act
        viewModel.unbind()

        // Assert
        assertNull(viewModel.surfaceRequest.value)
    }

    @Test
    fun `tapToFocus before the surface is available completes without a metering factory`() {
        // Arrange
        val viewModel = BarcodeScannerViewModel(formats = emptyList())

        // Act
        val result = runCatching { viewModel.tapToFocus(Offset(x = 10f, y = 20f)) }

        // Assert
        assertTrue(result.isSuccess, "a missing metering factory must short-circuit the tap")
    }
}
