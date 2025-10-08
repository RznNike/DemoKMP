package ru.rznnike.demokmp.domain.interactor.comobjectexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway

class OpenFolderOrFileUseCase(
    private val comObjectExampleGateway: ComObjectExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<String, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: String) = comObjectExampleGateway.openFolderOrFile(parameters)
}