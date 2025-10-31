package ru.rznnike.demokmp.domain.interactor.pdfexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File

class SavePdfToFileUseCase(
    private val pdfExampleGateway: PdfExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<SavePdfToFileUseCase.Parameters, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) = pdfExampleGateway.savePdfToFile(
        tempPdfFile = parameters.tempPdfFile,
        saveFile = parameters.saveFile
    )

    data class Parameters(
        val tempPdfFile: File,
        val saveFile: File
    )
}