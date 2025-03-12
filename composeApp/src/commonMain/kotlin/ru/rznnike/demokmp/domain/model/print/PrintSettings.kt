package ru.rznnike.demokmp.domain.model.print

data class PrintSettings(
    val printerName: String = "",
    val twoSidedPrint: TwoSidedPrint = TwoSidedPrint.default
)
