package ru.rznnike.demokmp.app.ui.screen.pdfexample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.model.common.HotkeyDescription
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.ui.viewmodel.common.print.FilePrintViewModel
import ru.rznnike.demokmp.app.ui.window.LocalWindow
import ru.rznnike.demokmp.app.utils.printDialog
import ru.rznnike.demokmp.app.viewmodel.pdfexample.PdfExampleViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class PdfExampleScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val dispatcherProvider: DispatcherProvider = koinInject()

        val viewModel = viewModel { PdfExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        val filePrintViewModel = viewModel {
            FilePrintViewModel()
        }
        val filePrintUiState by filePrintViewModel.uiState.collectAsState()

        val window = LocalWindow.current
        val fileSaver = rememberFileSaverLauncher(
            dialogSettings = FileKitDialogSettings(
                parentWindow = window
            )
        ) { result ->
            result?.file?.let {
                viewModel.savePdfToFile(it)
            }
        }

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.P) -> {
                        uiState.pdf?.let { pdf ->
                            val newPrinterName = printDialog(
                                pdf = pdf,
                                printSettings = filePrintUiState.printSettings
                            )
                            filePrintViewModel.onPrinterSelected(newPrinterName)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    Tooltip("Ctrl+W") {
                        SelectableOutlinedIconButton(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(40.dp),
                            iconRes = Res.drawable.ic_back,
                            onClick = {
                                navigator.closeScreen()
                            }
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    TextR(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        textRes = Res.string.pdf_example,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(8.dp))
                    PdfPrintControls(
                        modifier = Modifier.padding(vertical = 8.dp),
                        pdf = uiState.pdf,
                        printSettings = filePrintUiState.printSettings,
                        onTwoSidedPrintChanged = filePrintViewModel::onTwoSidedPrintChanged,
                        onPrinterSelected = filePrintViewModel::onPrinterSelected,
                        onSaveClick = {
                            fileSaver.launch(
                                suggestedName = viewModel.getSuggestedSaveFileName(),
                                extension = DataConstants.PDF_FILE_NAME_EXTENSION
                            )
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                }
            }
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PdfViewer(
                        modifier = Modifier.fillMaxSize(),
                        file = uiState.pdf,
                        isError = uiState.isError,
                        errorText = if (uiState.isError) {
                            stringResource(Res.string.error_file_not_found).format(DataConstants.TEST_PDF_PATH)
                        } else {
                            null
                        },
                        loadingContext = dispatcherProvider.io
                    )
                }
            }
        }
    }

    @Composable
    override fun getHotkeysDescription(): List<HotkeyDescription> = listOf(
        HotkeyDescription(
            hotkey = "Ctrl+P",
            description = stringResource(Res.string.hotkey_file_print)
        )
    )
}