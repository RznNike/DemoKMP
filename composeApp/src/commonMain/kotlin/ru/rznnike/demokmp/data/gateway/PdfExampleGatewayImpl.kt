package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File

class PdfExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider
) : PdfExampleGateway {
    override suspend fun getSamplePdf(): File = withContext(dispatcherProvider.io) {
        val result = File(DataConstants.TEST_PDF_PATH)
        if (!result.exists()) {
            throw NoSuchFileException(result)
        }
        result
    }

    override suspend fun savePdfToFile(tempPdfFile: File, saveFile: File): Unit = withContext(dispatcherProvider.io) {
        tempPdfFile.copyTo(
            target = saveFile,
            overwrite = true
        )
    }
}