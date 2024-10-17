package ru.rznnike.demokmp.domain.interactor.pdfexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File

class GetSamplePdfUseCase(
    private val pdfExampleGateway: PdfExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<File>(dispatcherProvider) {
    override suspend fun execute() = pdfExampleGateway.getSamplePdf()
}