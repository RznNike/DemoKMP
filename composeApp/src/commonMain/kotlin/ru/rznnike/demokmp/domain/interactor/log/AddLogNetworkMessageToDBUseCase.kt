package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogMessage

class AddLogNetworkMessageToDBUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<NetworkLogMessage, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: NetworkLogMessage) = logGateway.addLogNetworkMessageToDB(parameters)
}