package ru.rznnike.demokmp.domain.interactor.app

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.AppGateway

class CheckIfAppIsAlreadyRunningUseCase(
    private val appGateway: AppGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Boolean>(dispatcherProvider) {
    override suspend fun execute() = appGateway.checkIfAppIsAlreadyRunning()
}