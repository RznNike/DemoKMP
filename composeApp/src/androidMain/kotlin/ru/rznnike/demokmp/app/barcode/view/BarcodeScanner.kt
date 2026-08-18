package ru.rznnike.demokmp.app.barcode.view

import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rznnike.demokmp.app.barcode.viewmodel.BarcodeScannerViewModel

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun BarcodeScanner(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    formats: List<Int>,
    isEnabled: Boolean = true,
    onAnalyze: (String) -> Unit
) {
    val viewModel = viewModel { BarcodeScannerViewModel(formats) }
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.scrim)
    ) {
        surfaceRequest?.let { surfaceRequest ->
            val coordinateTransformer = remember { MutableCoordinateTransformer() }
            CameraXViewfinder(
                modifier = modifier.pointerInput(coordinateTransformer) {
                    detectTapGestures { tapCoordinates ->
                        with(coordinateTransformer) {
                            viewModel.tapToFocus(tapCoordinates.transform())
                        }
                    }
                },
                surfaceRequest = surfaceRequest,
                coordinateTransformer = coordinateTransformer
            )
        }
    }

    LaunchedEffect(lifecycleOwner, isEnabled, onAnalyze) {
        if (isEnabled) {
            viewModel.bindToCamera(
                context = context.applicationContext,
                lifecycleOwner = lifecycleOwner,
                onAnalyze = { result ->
                    onAnalyze(result)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            )
        } else {
            viewModel.unbind()
        }
    }
}