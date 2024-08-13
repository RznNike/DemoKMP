package ru.rznnike.demokmp.app.common.notifier

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider

class Notifier(
    private val coroutineScopeProvider: CoroutineScopeProvider
) {
    private val notifierFlow = MutableSharedFlow<SystemMessage>()

    fun subscribe() = notifierFlow.asSharedFlow()

    fun sendMessage(text: String) = emitMessage(
        SystemMessage(
            text = text,
            type = SystemMessage.Type.BAR
        )
    )

    fun sendMessage(textRes: StringResource) = emitMessage(
        SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.BAR
        )
    )

    fun sendAlert(text: String) = emitMessage(
        SystemMessage(
            text = text,
            type = SystemMessage.Type.ALERT
        )
    )

    fun sendAlert(textRes: StringResource) = emitMessage(
        SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.ALERT
        )
    )

    fun sendActionMessage(
        textRes: StringResource,
        actionTextRes: StringResource,
        actionCallback: () -> Unit
    ) = emitMessage(
        SystemMessage(
            textRes = textRes,
            actionTextRes = actionTextRes,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR
        )
    )

    fun sendActionMessage(
        text: String,
        actionText: String,
        actionCallback: () -> Unit
    ) = emitMessage(
        SystemMessage(
            text = text,
            actionText = actionText,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR
        )
    )

    private fun emitMessage(message: SystemMessage) {
        coroutineScopeProvider.io.launch {
            notifierFlow.emit(message)
        }
    }
}
