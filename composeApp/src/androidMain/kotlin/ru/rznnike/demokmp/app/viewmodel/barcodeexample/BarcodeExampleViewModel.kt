package ru.rznnike.demokmp.app.viewmodel.barcodeexample

import androidx.compose.runtime.Immutable
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.update
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel

class BarcodeExampleViewModel : BaseUiViewModel<BarcodeExampleViewModel.UiState>() {
    override fun provideDefaultUIState() = UiState()

    fun onScanResult(barcodeText: String) {
        setScannerEnabled(false)
        mutableUiState.update { currentState ->
            currentState.copy(
                lastScanResult = barcodeText
            )
        }
    }

    fun continueScan() = setScannerEnabled(true)

    private fun setScannerEnabled(newValue: Boolean) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isScannerEnabled = newValue
            )
        }
    }

    @Immutable
    data class UiState(
        val isScannerEnabled: Boolean = true,
        val barcodeFormats: List<Int> = listOf(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_CODE_128
        ),
        val lastScanResult: String = ""
    )
}