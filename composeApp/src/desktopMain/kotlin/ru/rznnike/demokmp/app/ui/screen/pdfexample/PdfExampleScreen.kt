package ru.rznnike.demokmp.app.ui.screen.pdfexample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.nameRes
import ru.rznnike.demokmp.app.utils.printDialog
import ru.rznnike.demokmp.app.viewmodel.pdfexample.PdfExampleViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import ru.rznnike.demokmp.generated.resources.*
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error_file_not_found
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.pdf_example

class PdfExampleScreen : NavigationScreen() {
    @OptIn(ExperimentalFoundationApi::class)
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

        Column {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically)
                        .onClick {
                            viewModel.onBackPressed()
                        },
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(Res.drawable.ic_back),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(16.dp))
                TextR(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textRes = Res.string.pdf_example,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(8.dp))

                DropdownSelector(
                    modifier = Modifier
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
                        .padding(vertical = 8.dp)
                        .size(40.dp),
                    iconRes = Res.drawable.ic_print,
                    onClick = {
                        uiState.pdf?.let { pdf ->
                            val newPrinterName = printDialog(
                                pdf = pdf,
                                printSettings = uiState.printSettings
                            )
                            viewModel.onPrinterSelected(newPrinterName)
                        }
                    }
                )
                Spacer(Modifier.width(4.dp))
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
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