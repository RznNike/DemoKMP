package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.FlowUseCase
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogNetworkMessage

class GetNetworkLogUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : FlowUseCase<List<LogNetworkMessage>>(dispatcherProvider) {
    override suspend fun execute() = logGateway.getNetworkLog()
}