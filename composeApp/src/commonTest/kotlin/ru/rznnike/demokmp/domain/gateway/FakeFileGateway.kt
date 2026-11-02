package ru.rznnike.demokmp.domain.gateway

import java.io.File

/**
 * Test double for [FileGateway].
 *
 * Every copy is recorded in [copiedFiles] so tests can assert the forwarded file pair.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeFileGateway : FileGateway {
    var errorToThrow: Exception? = null

    val copiedFiles = mutableListOf<Pair<File, File>>()

    override suspend fun copyFile(original: File, copy: File) {
        errorToThrow?.let { throw it }
        copiedFiles += original to copy
    }
}
