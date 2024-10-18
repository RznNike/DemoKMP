package ru.rznnike.demokmp.app.dispatcher.event

sealed class AppEvent {
    data object TestObject : AppEvent()

    data class TestClass(
        val data: String
    ) : AppEvent()
}
