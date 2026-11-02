package ru.rznnike.demokmp.app.error

interface ErrorHandler {
    suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit)
}
