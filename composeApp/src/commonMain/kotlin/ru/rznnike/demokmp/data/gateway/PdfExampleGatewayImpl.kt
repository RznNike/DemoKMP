package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File

class PdfExampleGatewayImpl: PdfExampleGateway {
    override suspend fun getSamplePdf(): File {
        val result = File(DataConstants.TEST_PDF_PATH)
        if (!result.exists()) {
            throw NoSuchFileException(result)
        }
        return result
    }

    override suspend fun savePdfToFile(tempPdfFile: File, saveFile: File) {
        tempPdfFile.copyTo(
            target = saveFile,
            overwrite = true
        )
    }
}