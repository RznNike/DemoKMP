package ru.rznnike.demokmp.app.viewmodel.pdfexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import java.io.File

class PdfExampleViewModel(
    private val navigationCallback: (NavigationCommand) -> Unit
) : BaseUiViewModel<PdfExampleViewModel.UiState>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val getPrintSettingsUseCase: GetPrintSettingsUseCase by inject()
    private val setPrintSettingsUseCase: SetPrintSettingsUseCase by inject()
    private val getSamplePdfUseCase: GetSamplePdfUseCase by inject()

    init {
        loadPreferences()
        loadDocument()
    }

    override fun provideDefaultUIState() = UiState()

    private fun loadPreferences() {
        viewModelScope.launch {
            getPrintSettingsUseCase().process(
                { result ->
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            printSettings = result
                        )
                    }
                }, ::onError
            )
        }
    }

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

    fun onTwoSidedPrintChanged(newValue: TwoSidedPrint) {
        if (newValue == mutableUiState.value.printSettings.twoSidedPrint) return

        val newPrintSettings = mutableUiState.value.printSettings.copy(
            twoSidedPrint = newValue
        )
        updatePrintSettings(newPrintSettings)
    }

    fun onPrinterSelected(printerName: String) {
        if (printerName == mutableUiState.value.printSettings.printerName) return

        val newPrintSettings = mutableUiState.value.printSettings.copy(
            printerName = printerName
        )
        updatePrintSettings(newPrintSettings)
    }

    private fun updatePrintSettings(newValue: PrintSettings) {
        viewModelScope.launch {
            mutableUiState.update { currentState ->
                currentState.copy(
                    printSettings = newValue
                )
            }
            setPrintSettingsUseCase(newValue).process(
                { }, ::onError
            )
        }
    }

    private suspend fun onError(error: Throwable) {
        errorHandler.proceed(error) { message ->
            notifier.sendAlert(message)
        }
    }

    data class UiState(
        val pdf: File? = null,
        val isError: Boolean = false,
        val printSettings: PrintSettings = PrintSettings(),
    )

    sealed class NavigationCommand {
        data object Back : NavigationCommand()
    }
}