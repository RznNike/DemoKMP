package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.common.UiScale

class GetUiScaleUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<UiScale>(dispatcherProvider) {
    override suspend fun execute() = preferencesGateway.getUiScale()
}