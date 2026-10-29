package ru.rznnike.demokmp.app.error

/**
 * Test double for [ErrorHandler].
 *
 * Records the errors a ViewModel routes to the error handler; callbacks are not invoked, because the
 * exception → user-message mapping is covered by `ErrorHandlerTest`.
 */
class FakeErrorHandler : ErrorHandler {
    val proceededErrors = mutableListOf<Throwable>()

    override suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit) {
        proceededErrors += error
    }
}
