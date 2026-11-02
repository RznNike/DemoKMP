package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.CompletableDeferred
import ru.rznnike.demokmp.domain.model.db.DBExampleData

/**
 * Test double for [DBExampleGateway].
 *
 * Mutations are recorded in collections, so tests can assert what was forwarded to the gateway.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 *
 * Set [gate] to suspend [getAll] until the test completes it — the only way to observe a caller's
 * intermediate state (`isLoading`) while the list request is in flight.
 */
class FakeDBExampleGateway : DBExampleGateway {
    var errorToThrow: Exception? = null

    val requestedIds = mutableListOf<Long>()
    var getResult: DBExampleData? = null

    var allResult: List<DBExampleData> = emptyList()
    var getAllCalls = 0
    var gate: CompletableDeferred<Unit>? = null
    val addedData = mutableListOf<DBExampleData>()
    val deletedData = mutableListOf<DBExampleData>()
    var deleteAllCalls = 0
    var closeDBCalls = 0

    override suspend fun get(id: Long): DBExampleData? {
        errorToThrow?.let { throw it }
        requestedIds += id
        return getResult
    }

    override suspend fun getAll(): List<DBExampleData> {
        errorToThrow?.let { throw it }
        getAllCalls++
        gate?.await()
        return allResult
    }

    override suspend fun add(data: DBExampleData) {
        errorToThrow?.let { throw it }
        addedData += data
    }

    override suspend fun delete(data: DBExampleData) {
        errorToThrow?.let { throw it }
        deletedData += data
    }

    override suspend fun deleteAll() {
        errorToThrow?.let { throw it }
        deleteAllCalls++
    }

    override suspend fun closeDB() {
        errorToThrow?.let { throw it }
        closeDBCalls++
    }
}
