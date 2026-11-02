package ru.rznnike.demokmp.domain.gateway

/**
 * Test double for [AppGateway].
 *
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeAppGateway : AppGateway {
    var errorToThrow: Exception? = null

    var alreadyRunning: Boolean = false
    var closeSingleInstanceSocketCalls = 0

    override suspend fun checkIfAppIsAlreadyRunning(): Boolean {
        errorToThrow?.let { throw it }
        return alreadyRunning
    }

    override suspend fun closeAppSingleInstanceSocket() {
        errorToThrow?.let { throw it }
        closeSingleInstanceSocketCalls++
    }
}
