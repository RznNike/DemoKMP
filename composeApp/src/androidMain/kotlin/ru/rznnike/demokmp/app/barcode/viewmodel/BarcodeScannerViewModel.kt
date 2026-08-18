package ru.rznnike.demokmp.app.barcode.viewmodel

import android.content.Context
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.barcode.utils.BarcodeAnalyzer
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel
import java.time.Clock
import java.util.concurrent.Executors

@ExperimentalCamera2Interop
class BarcodeScannerViewModel(
    private val formats: List<Int>
) : BaseViewModel() {
    private val clock: Clock by inject()

    private val mutableSurfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = mutableSurfaceRequest.asStateFlow()
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null
    private var unbindCallback: () -> Unit = {}

    private val cameraPreviewUseCase = Preview.Builder()
        .build()
        .apply {
            setSurfaceProvider { newSurfaceRequest ->
                mutableSurfaceRequest.update { newSurfaceRequest }
                surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                    newSurfaceRequest.resolution.width.toFloat(),
                    newSurfaceRequest.resolution.height.toFloat()
                )
            }
        }

    suspend fun bindToCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onAnalyze: (String) -> Unit
    ) {
        val cameraProvider = ProcessCameraProvider.awaitInstance(context)

        val cameraExecutor = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(
                    cameraExecutor,
                    BarcodeAnalyzer(
                        clock = clock,
                        formats = formats,
                        onAnalyze = onAnalyze
                    )
                )
            }

        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            cameraPreviewUseCase,
            imageAnalysis
        )
        cameraControl = camera.cameraControl
        unbindCallback = {
            cameraProvider.unbindAll()
            cameraControl = null
            cameraExecutor.shutdown()
        }

        try {
            awaitCancellation()
        } finally {
            unbindCallback()
        }
    }

    fun unbind() = unbindCallback()

    fun tapToFocus(tapCoordinates: Offset) {
        surfaceMeteringPointFactory
            ?.createPoint(tapCoordinates.x, tapCoordinates.y)
            ?.let { point ->
                val meteringAction = FocusMeteringAction.Builder(point).build()
                cameraControl?.startFocusAndMetering(meteringAction)
            }
    }
}