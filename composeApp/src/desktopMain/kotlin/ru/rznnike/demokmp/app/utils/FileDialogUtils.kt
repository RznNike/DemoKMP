package ru.rznnike.demokmp.app.utils

import androidx.compose.ui.awt.ComposeWindow
import org.apache.pdfbox.Loader
import org.apache.pdfbox.printing.PDFPageable
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import java.awt.FileDialog
import java.awt.print.PrinterException
import java.awt.print.PrinterJob
import java.io.File
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.DialogTypeSelection
import javax.print.attribute.standard.Sides

fun saveFileDialog(
    window: ComposeWindow,
    title: String,
    fileName: String
): File? {
    return FileDialog(window, title, FileDialog.SAVE).apply {
        file = fileName
        val extension = fileName.substringAfterLast('.')
        setFilenameFilter { _, name ->
            name.endsWith(extension)
        }
        isVisible = true
    }.files.firstOrNull()
}

fun printDialog(
    pdf: File,
    printSettings: PrintSettings
): String {
    val printerJob = PrinterJob.getPrinterJob()

    val attributes = HashPrintRequestAttributeSet()
    attributes.add(
        when (printSettings.twoSidedPrint) {
            TwoSidedPrint.DISABLED -> Sides.ONE_SIDED
            TwoSidedPrint.TWO_SIDED_LONG_EDGE -> Sides.TWO_SIDED_LONG_EDGE
            TwoSidedPrint.TWO_SIDED_SHORT_EDGE -> Sides.TWO_SIDED_SHORT_EDGE
        }
    )
    attributes.add(DialogTypeSelection.NATIVE)

    val document = Loader.loadPDF(pdf)
    printerJob.setPageable(PDFPageable(document))

    PrinterJob.lookupPrintServices().firstOrNull { it.name == printSettings.printerName }?.let {
        printerJob.printService = it
    }

    var newPrinterName = printSettings.printerName
    if (printerJob.printDialog(attributes)) {
        try {
            newPrinterName = printerJob.printService.name
            printerJob.print(attributes)
        } catch (exception: PrinterException) {
            Logger.e(exception)
        }
    }

    return newPrinterName
}