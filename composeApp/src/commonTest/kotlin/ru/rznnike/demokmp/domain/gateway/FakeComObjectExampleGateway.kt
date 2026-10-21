package ru.rznnike.demokmp.domain.gateway

/**
 * Test double for [ComObjectExampleGateway].
 *
 * Every call is recorded in [calls] so tests can assert which command reached the gateway.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeComObjectExampleGateway : ComObjectExampleGateway {
    var errorToThrow: Exception? = null

    val calls = mutableListOf<String>()
    var pcData: String = ""

    override suspend fun initShellWrapper() {
        errorToThrow?.let { throw it }
        calls += "initShellWrapper"
    }

    override suspend fun destroyShellWrapper() {
        errorToThrow?.let { throw it }
        calls += "destroyShellWrapper"
    }

    override suspend fun getPCData(): String {
        errorToThrow?.let { throw it }
        calls += "getPCData"
        return pcData
    }

    override suspend fun openFolderOrFile(path: String) {
        errorToThrow?.let { throw it }
        calls += "openFolderOrFile:$path"
    }

    override suspend fun minimizeAllWindows() {
        errorToThrow?.let { throw it }
        calls += "minimizeAllWindows"
    }
}
