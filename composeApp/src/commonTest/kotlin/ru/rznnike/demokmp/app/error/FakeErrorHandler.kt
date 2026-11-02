package ru.rznnike.demokmp.app.error

/**
 * Test double for [ErrorHandler].
 *
 * Records the errors a ViewModel routes to the error handler.
 *
 * Callbacks are **not** invoked by default, because the exception → user-message mapping is covered
 * by `ErrorHandlerTest`. Pass `invokeCallbacks = true` when the production code performs its work
 * *inside* the callback (e.g. `PdfExampleViewModel` sets `isError` there), and the resulting
 * messages are recorded in [messages].
 */
class FakeErrorHandler(
    private val invokeCallbacks: Boolean = false
) : ErrorHandler {
    val proceededErrors = mutableListOf<Throwable>()
    val messages = mutableListOf<String>()

    override suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit) {
        proceededErrors += error

        if (invokeCallbacks) {
            val message = error.message ?: DEFAULT_MESSAGE
            messages += message
            callback(message)
        }
    }

    private companion object {
        const val DEFAULT_MESSAGE = "error"
    }
}
