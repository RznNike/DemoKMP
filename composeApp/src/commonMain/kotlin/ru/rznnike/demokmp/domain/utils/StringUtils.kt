package ru.rznnike.demokmp.domain.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

val decimal2Format = DecimalFormatSymbols().let {
    it.groupingSeparator = ' '
    it.decimalSeparator = '.'
    DecimalFormat("0.##", it)
}

val moneyDecimal2Format = getNumberFormat(2)

fun getNumberFormat(precision: Int, grouping: Boolean = true): DecimalFormat {
    val pattern = StringBuilder(
        if (grouping) "###,###,###,##0." else "0."
    ).apply {
        if (precision > 0) {
            for (i in 1..precision) {
                append("0")
            }
        } else {
            append("##")
        }
    }.toString()
    return DecimalFormatSymbols().let {
        it.groupingSeparator = ' '
        it.decimalSeparator = '.'
        DecimalFormat(pattern, it)
    }
}

fun String.toBigDecimalOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toBigDecimalOrNull()

fun String.toBigDecimalSmart(): BigDecimal = toBigDecimalOrNullSmart() ?: BigDecimal.ZERO

fun String.toDoubleOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toDoubleOrNull()
