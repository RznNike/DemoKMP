package ru.rznnike.demokmp.app.viewmodel.pdfexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import java.io.File

class PdfExampleViewModel(
    private val navigationCallback: (NavigationCommand) -> Unit
) : BaseUiViewModel<PdfExampleViewModel.UiState>() {
    private val errorHandler: ErrorHandler by inject()
    private val getSamplePdfUseCase: GetSamplePdfUseCase by inject()

    init {
        loadDocument()
    }

    override fun provideDefaultUIState() = UiState()

    private fun loadDocument() {
        viewModelScope.launch {
            getSamplePdfUseCase().process(
                { result ->
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            pdf = result,
                            isError = false
                        )
                    }
                }, { error ->
                    errorHandler.proceed(error) { message ->
                        mutableUiState.update { currentState ->
                            currentState.copy(
                                isError = true
                            )
                        }
                    }
                }
            )
        }
    }

    fun onBackPressed() {
        navigationCallback(NavigationCommand.Back)
    }

    data class UiState(
        val pdf: File? = null,
        val isError: Boolean = false
    )

    sealed class NavigationCommand {
        data object Back : NavigationCommand()
    }
}