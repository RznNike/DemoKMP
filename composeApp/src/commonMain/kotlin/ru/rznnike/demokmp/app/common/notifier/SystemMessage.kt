package ru.rznnike.demokmp.app.common.notifier

import org.jetbrains.compose.resources.StringResource

data class SystemMessage(
    val textRes: StringResource? = null,
    var text: String? = null,
    val actionTextRes: StringResource? = null,
    var actionText: String? = null,
    val actionCallback: (() -> Unit)? = null,
    val type: Type
) {
    enum class Type {
        ALERT,
        BAR
    }
}