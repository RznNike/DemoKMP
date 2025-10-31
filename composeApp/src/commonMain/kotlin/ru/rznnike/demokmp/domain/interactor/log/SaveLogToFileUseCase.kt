package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import java.io.File

class SaveLogToFileUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<File, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: File) = logGateway.saveLogToFile(parameters)
}