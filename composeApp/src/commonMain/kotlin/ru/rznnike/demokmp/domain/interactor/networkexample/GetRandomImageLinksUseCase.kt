package ru.rznnike.demokmp.domain.interactor.networkexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.NetworkExampleGateway

class GetRandomImageLinksUseCase(
    private val networkTestGateway: NetworkExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<GetRandomImageLinksUseCase.Parameters, List<String>>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) =
        networkTestGateway.getRandomImageLinks(
            count = parameters.count
        )

    data class Parameters(
        val count: Int
    )
}