package ru.rznnike.demokmp.domain.gateway

import java.io.File

/**
 * Test double for [PdfExampleGateway].
 *
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakePdfExampleGateway : PdfExampleGateway {
    var errorToThrow: Exception? = null

    var samplePdf: File = File("sample.pdf")

    override suspend fun getSamplePdf(): File {
        errorToThrow?.let { throw it }
        return samplePdf
    }
}
