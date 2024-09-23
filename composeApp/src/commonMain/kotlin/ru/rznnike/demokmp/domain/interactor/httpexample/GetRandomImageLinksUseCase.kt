package ru.rznnike.demokmp.domain.interactor.httpexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway

class GetRandomImageLinksUseCase(
    private val httpExampleGateway: HTTPExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<GetRandomImageLinksUseCase.Parameters, List<String>>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) = httpExampleGateway.getRandomImageLinks(
        count = parameters.count
    )

    data class Parameters(
        val count: Int
    )
}