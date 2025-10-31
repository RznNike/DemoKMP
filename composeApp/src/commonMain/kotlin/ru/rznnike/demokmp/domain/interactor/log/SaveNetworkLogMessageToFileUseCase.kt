package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import java.io.File

class SaveNetworkLogMessageToFileUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<SaveNetworkLogMessageToFileUseCase.Parameters, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) = logGateway.saveNetworkLogMessageToFile(
        file = parameters.file,
        message = parameters.message
    )

    data class Parameters(
        val file: File,
        val message: NetworkLogMessage
    )
}