package ru.rznnike.demokmp.app.dispatcher.event

sealed class AppEvent {
    data object RestartRequested : AppEvent()

    data class TestClass(
        val data: String
    ) : AppEvent()
}
