package ru.rznnike.demokmp.app.viewmodel.chartexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.interactor.chartexample.GetChartSampleDataUseCase
import ru.rznnike.demokmp.domain.model.chart.ChartPoint

class ChartExampleViewModel : BaseUiViewModel<ChartExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val getChartSampleDataUseCase: GetChartSampleDataUseCase by inject()

    init {
        loadData()
    }

    override fun provideDefaultUIState() = UiState()

    private fun loadData() {
        viewModelScope.launch {
            getChartSampleDataUseCase().process(
                { result ->
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            data = result
                        )
                    }
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    data class UiState(
        val data: List<ChartPoint> = emptyList()
    )
}