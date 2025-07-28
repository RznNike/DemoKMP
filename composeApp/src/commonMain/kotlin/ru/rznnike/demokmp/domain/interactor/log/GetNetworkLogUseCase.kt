package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.NetworkLogData

class GetNetworkLogUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<NetworkLogData>(dispatcherProvider) {
    override suspend fun execute() = logGateway.getNetworkLog()
}