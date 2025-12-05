package ru.rznnike.demokmp.app.dispatcher.event

sealed class AppEvent {
    data object RestartRequested : AppEvent()

    data object ActivityRestartRequested : AppEvent()

    data class BottomStatusBarRequested(
        val show: Boolean
    ) : AppEvent()

    data object LoggerWindowRequested : AppEvent()
}
