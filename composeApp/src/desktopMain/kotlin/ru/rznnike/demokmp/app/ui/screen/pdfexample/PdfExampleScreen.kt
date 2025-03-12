package ru.rznnike.demokmp.app.ui.screen.pdfexample

import androidx.compose.foundation.ExperimentalFoundationApi
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.DropdownSelector
import ru.rznnike.demokmp.app.ui.view.PdfViewer
import ru.rznnike.demokmp.app.ui.view.SelectableOutlinedIconButton
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.utils.nameRes
import ru.rznnike.demokmp.app.utils.printDialog
import ru.rznnike.demokmp.app.viewmodel.pdfexample.PdfExampleViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import ru.rznnike.demokmp.generated.resources.*

class PdfExampleScreen : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val dispatcherProvider: DispatcherProvider = koinInject()

        val viewModel = viewModel {
            PdfExampleViewModel { command ->
                when (command) {
                    is PdfExampleViewModel.NavigationCommand.Back -> {
                        navigator.closeScreen()
                    }
                }
            }
        }
        val uiState by viewModel.uiState.collectAsState()

        fun openPrintDialog() {
            uiState.pdf?.let { pdf ->
                val newPrinterName = printDialog(
                    pdf = pdf,
                    printSettings = uiState.printSettings
                )
                viewModel.onPrinterSelected(newPrinterName)
            }
        }

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.P) -> openPrintDialog()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                    SelectableOutlinedIconButton(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(40.dp),
                        iconRes = Res.drawable.ic_back,
                        onClick = {
                            viewModel.onBackPressed()
                        }
                    )
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
                    DropdownSelector(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 16.dp)
                            .width(210.dp),
                        label = stringResource(Res.string.two_sided_print),
                        items = TwoSidedPrint.entries,
                        selectedItem = uiState.printSettings.twoSidedPrint,
                        itemNameRetriever = { it?.let { stringResource(it.nameRes) } ?: "" },
                        onItemSelected = viewModel::onTwoSidedPrintChanged
                    )
                    Spacer(Modifier.width(16.dp))
                    SelectableOutlinedIconButton(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(40.dp),
                        iconRes = Res.drawable.ic_print,
                        onClick = ::openPrintDialog
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
}