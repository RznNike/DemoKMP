package ru.rznnike.demokmp.domain.interactor.comobjectexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway

class InitShellWrapperUseCase(
    private val comObjectExampleGateway: ComObjectExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider) {
    override suspend fun execute() = comObjectExampleGateway.initShellWrapper()
}