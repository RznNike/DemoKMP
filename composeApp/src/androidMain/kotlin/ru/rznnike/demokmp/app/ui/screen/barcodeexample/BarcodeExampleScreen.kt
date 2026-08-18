package ru.rznnike.demokmp.app.ui.screen.barcodeexample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.barcode.view.BarcodeScanner
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.permission.rememberCameraPermissionHandler
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.cardBackground
import ru.rznnike.demokmp.app.utils.openAppSettings
import ru.rznnike.demokmp.app.utils.statusBarsAndCutoutPadding
import ru.rznnike.demokmp.app.viewmodel.barcodeexample.BarcodeExampleViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class BarcodeExampleScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { BarcodeExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        val notifier = koinInject<Notifier>()

        val context = LocalContext.current
        val cameraPermissionHandler = rememberCameraPermissionHandler()
        var cameraPermissionGranted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            cameraPermissionHandler.checkPermissions { permissionsGranted ->
                cameraPermissionGranted = permissionsGranted
                if (!permissionsGranted) {
                    notifier.sendActionMessage(Res.string.error_barcode_camera_permission, Res.string.settings) {
                        context.openAppSettings()
                    }
                    navigator.closeScreen()
                }
            }
        }

        val showResultDialog = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .statusBarsAndCutoutPadding()
                .navigationBarsPadding()
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.barcode_scanner_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            BarcodeScanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .cardBackground(),
                formats = uiState.barcodeFormats,
                isEnabled = cameraPermissionGranted && uiState.isScannerEnabled,
                onAnalyze = { result ->
                    viewModel.onScanResult(result)
                    showResultDialog.value = true
                }
            )
        }

        CommonAlertDialog(
            showDialog = showResultDialog,
            type = AlertDialogType.HORIZONTAL,
            header = stringResource(Res.string.scan_result),
            message = uiState.lastScanResult,
            cancellable = false,
            actions = listOf(
                AlertDialogAction(stringResource(Res.string.continue_title)) {
                    showResultDialog.value = false
                    viewModel.continueScan()
                },
                AlertDialogAction(stringResource(Res.string.close)) {
                    showResultDialog.value = false
                    navigator.closeScreen()
                }
            )
        )
    }
}