package ru.rznnike.demokmp.domain.interactor.app

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.AppGateway

class CloseAppSingleInstanceSocketUseCase(
    private val appGateway: AppGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider) {
    override suspend fun execute() = appGateway.closeAppSingleInstanceSocket()
}