package ru.rznnike.demokmp.domain.log

sealed class LogEvent {
    data object NewMessage : LogEvent()

    data class NewNetworkMessage(val message: LogNetworkMessage) : LogEvent()

    data object Cleanup : LogEvent()
}