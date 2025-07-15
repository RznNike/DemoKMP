package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.LogGateway

class ClearLogUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider) {
    override suspend fun execute() = logGateway.clearLog()
}