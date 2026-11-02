package ru.rznnike.demokmp.app.viewmodel.barcodeexample

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `BarcodeExampleViewModel` lives in `androidMain`, so this test belongs in `androidUnitTest`
 * (task `testDebugUnitTest`). The ViewModel has no collaborators and no coroutines — no Koin module
 * and no `ViewModelTestRule` are needed.
 *
 * `barcodeFormats` is deliberately not asserted: it is built from `Barcode.FORMAT_*` constants, and
 * the Android host JVM runs against stubbed `android.jar` (`isReturnDefaultValues = true`), so those
 * values say nothing about this class.
 */
class BarcodeExampleViewModelTest {
    @Test
    fun `default state enables the scanner with an empty last result`() {
        // Act
        val viewModel = BarcodeExampleViewModel()

        // Assert
        assertTrue(viewModel.uiState.value.isScannerEnabled)
        assertEquals("", viewModel.uiState.value.lastScanResult)
    }

    @Test
    fun `onScanResult disables the scanner and stores the result`() {
        // Arrange
        val viewModel = BarcodeExampleViewModel()

        // Act
        viewModel.onScanResult("qr-payload")

        // Assert
        assertFalse(viewModel.uiState.value.isScannerEnabled)
        assertEquals("qr-payload", viewModel.uiState.value.lastScanResult)
    }

    @Test
    fun `continueScan re-enables the scanner`() {
        // Arrange
        val viewModel = BarcodeExampleViewModel()
        viewModel.onScanResult("qr-payload")

        // Act
        viewModel.continueScan()

        // Assert
        assertTrue(viewModel.uiState.value.isScannerEnabled)
    }
}
