package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.CompletableDeferred

/**
 * Test double for [HTTPExampleGateway].
 *
 * Every requested count is recorded in [requestedCounts] so tests can assert the forwarded argument.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 *
 * Set [gate] to suspend the call until the test completes it — the only way to observe a caller's
 * intermediate state (`isLoading`, cleared images) while a request is in flight.
 */
class FakeHTTPExampleGateway : HTTPExampleGateway {
    var errorToThrow: Exception? = null

    val requestedCounts = mutableListOf<Int>()
    var links: List<String> = emptyList()
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun getRandomImageLinks(count: Int): List<String> {
        errorToThrow?.let { throw it }
        requestedCounts += count
        gate?.await()
        return links
    }
}
