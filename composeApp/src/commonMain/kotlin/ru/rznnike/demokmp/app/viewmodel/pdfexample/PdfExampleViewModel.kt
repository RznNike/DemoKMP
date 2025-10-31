package ru.rznnike.demokmp.app.viewmodel.pdfexample

import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import ru.rznnike.demokmp.domain.interactor.pdfexample.SavePdfToFileUseCase
import java.io.File

class PdfExampleViewModel : BaseUiViewModel<PdfExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getSamplePdfUseCase: GetSamplePdfUseCase by inject()
    private val savePdfToFileUseCase: SavePdfToFileUseCase by inject()

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
                    errorHandler.proceed(error) {
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

    fun openSaveFileDialog(showFileDialog: suspend (String) -> PlatformFile?) {
        coroutineScopeProvider.io.launch {
            val suggestedFileName = DataConstants.EXAMPLE_PDF_SAVE_NAME
            showFileDialog(suggestedFileName)?.let { platformFile ->
                mutableUiState.value.pdf?.let { pdf ->
                    savePdfToFileUseCase(
                        SavePdfToFileUseCase.Parameters(
                            tempPdfFile = pdf,
                            saveFile = platformFile.file
                        )
                    ).process(
                        { }, { error ->
                            errorHandler.proceed(error) { message ->
                                notifier.sendAlert(message)
                            }
                        }
                    )
                }
            }
        }
    }

    data class UiState(
        val pdf: File? = null,
        val isError: Boolean = false
    )
}