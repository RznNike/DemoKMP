package ru.rznnike.demokmp.domain.interactor.file

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.FileGateway
import java.io.File

class CopyFileUseCase(
    private val fileGateway: FileGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<CopyFileUseCase.Parameters, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) = fileGateway.copyFile(
        original = parameters.original,
        copy = parameters.copy
    )

    data class Parameters(
        val original: File,
        val copy: File
    )
}