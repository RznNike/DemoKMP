package ru.rznnike.demokmp.app.ui.dialog

data class AlertDialogAction(
    val text: String,
    val accent: Boolean = true,
    val callback: () -> Unit
)
