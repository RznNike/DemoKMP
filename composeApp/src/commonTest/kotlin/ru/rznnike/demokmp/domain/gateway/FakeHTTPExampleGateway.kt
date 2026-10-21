package ru.rznnike.demokmp.domain.gateway

/**
 * Test double for [HTTPExampleGateway].
 *
 * Every requested count is recorded in [requestedCounts] so tests can assert the forwarded argument.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeHTTPExampleGateway : HTTPExampleGateway {
    var errorToThrow: Exception? = null

    val requestedCounts = mutableListOf<Int>()
    var links: List<String> = emptyList()

    override suspend fun getRandomImageLinks(count: Int): List<String> {
        errorToThrow?.let { throw it }
        requestedCounts += count
        return links
    }
}
