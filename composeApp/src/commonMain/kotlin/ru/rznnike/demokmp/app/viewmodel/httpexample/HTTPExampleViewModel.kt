package ru.rznnike.demokmp.app.viewmodel.httpexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.httpexample.GetRandomImageLinksUseCase

private const val IMAGES_COUNT = 6

class HTTPExampleViewModel : BaseUiViewModel<HTTPExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val getRandomImageLinksUseCase: GetRandomImageLinksUseCase by inject()

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            requestImages()
        }
    }

    override fun provideDefaultUIState() = UiState()

    override fun onProgressStateChanged(show: Boolean) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isLoading = show
            )
        }
    }

    private fun setImages(images: List<String>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                images = images
            )
        }
    }

    fun requestImages() {
        viewModelScope.launch {
            setProgress(show = true, immediately = true)
            setImages(emptyList())
            getRandomImageLinksUseCase(
                GetRandomImageLinksUseCase.Parameters(
                    count = IMAGES_COUNT
                )
            ).process(
                { result ->
                    setImages(result)
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        notifier.sendAlert(message)
                    }
                }
            )
            setProgress(show = false, immediately = true)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val images: List<String> = emptyList()
    )
}