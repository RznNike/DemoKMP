package ru.rznnike.demokmp.app.viewmodel.networkexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.interactor.networkexample.GetRandomImageLinksUseCase

class NetworkExampleViewModel : BaseUiViewModel<NetworkExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val getRandomImageLinksUseCase: GetRandomImageLinksUseCase by inject()

    override fun provideDefaultUIState() = UiState()

    private fun setProgress(isLoading: Boolean) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }

    private fun setImages(images: List<String>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                images = images
            )
        }
    }

    fun requestImages() {
        viewModelScope.launch {
            setProgress(true)
            getRandomImageLinksUseCase(
                GetRandomImageLinksUseCase.Parameters(
                    count = 3
                )
            ).process(
                { result ->
                    setImages(result)
                }, { error ->
                    setProgress(false)
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val images: List<String> = emptyList()
    )
}