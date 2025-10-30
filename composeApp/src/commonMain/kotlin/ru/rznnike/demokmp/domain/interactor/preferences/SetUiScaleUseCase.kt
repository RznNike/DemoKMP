package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.common.UiScale

class SetUiScaleUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<UiScale, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: UiScale) = preferencesGateway.setUiScale(parameters)
}