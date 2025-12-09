package ru.rznnike.demokmp.app.ui.viewmodel.common.print

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.interactor.file.CopyFileUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import java.io.File

class FilePrintViewModel : BaseUiViewModel<FilePrintViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getPrintSettingsUseCase: GetPrintSettingsUseCase by inject()
    private val setPrintSettingsUseCase: SetPrintSettingsUseCase by inject()
    private val copyFileUseCase: CopyFileUseCase by inject()

    init {
        loadPreferences()
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

    fun saveFile(original: File, copy: File) {
        coroutineScopeProvider.io.launch {
            copyFileUseCase(
                CopyFileUseCase.Parameters(
                    original = original,
                    copy = copy
                )
            ).process(
                { }, ::onError
            )
        }
    }

    data class UiState(
        val printSettings: PrintSettings = PrintSettings()
    )
}