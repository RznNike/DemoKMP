package ru.rznnike.demokmp.app.ui.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.ui.theme.PreviewAppTheme
import ru.rznnike.demokmp.app.utils.nameRes
import ru.rznnike.demokmp.app.utils.printDialog
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_print
import ru.rznnike.demokmp.generated.resources.ic_save
import ru.rznnike.demokmp.generated.resources.two_sided_print
import java.io.File

@Composable
fun PdfPrintControls(
    modifier: Modifier = Modifier,
    pdf: File?,
    printSettings: PrintSettings,
    onTwoSidedPrintChanged: (TwoSidedPrint) -> Unit,
    onPrinterSelected: (String) -> Unit,
    onSaveClick: () -> Unit
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    DropdownSelector(
        modifier = Modifier
            .width(220.dp)
            .padding(bottom = 8.dp),
        label = stringResource(Res.string.two_sided_print),
        items = TwoSidedPrint.entries,
        selectedItem = printSettings.twoSidedPrint,
        itemNameRetriever = { it?.let { stringResource(it.nameRes) } ?: "" },
        onItemSelected = onTwoSidedPrintChanged
    )

    Spacer(Modifier.width(16.dp))
    Tooltip("Ctrl+P") {
        SelectableOutlinedIconButton(
            modifier = Modifier.size(40.dp),
            iconRes = Res.drawable.ic_print,
            onClick = {
                pdf?.let {
                    val newPrinterName = printDialog(
                        pdf = pdf,
                        printSettings = printSettings
                    )
                    onPrinterSelected(newPrinterName)
                }
            }
        )
    }
    Spacer(Modifier.width(16.dp))
    SelectableOutlinedIconButton(
        modifier = Modifier.size(40.dp),
        iconRes = Res.drawable.ic_save,
        onClick = onSaveClick
    )
}

@Preview
@Composable
private fun PdfPrintControlsPreview() {
    PreviewAppTheme {
        PdfPrintControls(
            pdf = null,
            printSettings = PrintSettings(),
            onTwoSidedPrintChanged = { },
            onPrinterSelected = { },
            onSaveClick = { }
        )
    }
}