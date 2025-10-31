package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogMessage

class GetNewNetworkLogUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Long, List<NetworkLogMessage>>(dispatcherProvider) {
    override suspend fun execute(parameters: Long) = logGateway.getNewNetworkLog(parameters)
}