package ru.rznnike.demokmp.app.barcode.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.time.Clock

private const val SCAN_RESULT_THRESHOLD_MS = 500

class BarcodeAnalyzer(
    val clock: Clock,
    val formats: List<Int>,
    val onAnalyze: (barcode: String) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimeStamp = 0L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = clock.millis()
        if (currentTimestamp - lastAnalyzedTimeStamp >= SCAN_RESULT_THRESHOLD_MS) {
            lastAnalyzedTimeStamp = currentTimestamp
            val firstFormat = formats.firstOrNull() ?: Barcode.FORMAT_UNKNOWN
            val otherFormats = (formats - firstFormat).toIntArray()
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(firstFormat, *otherFormats)
                .build()
            val scanner = BarcodeScanning.getClient(options)
            val mediaImage = imageProxy.image
            mediaImage?.let {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes
                            .mapNotNull { it.rawValue }
                            .firstOrNull { it.isNotBlank() }
                            ?.let { result -> onAnalyze(result) }
                    }
                    .addOnFailureListener {
                        Log.d("BarcodeAnalyzer", "Something went wrong\n$it")
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        } else {
            imageProxy.close()
        }
    }
}