package ru.rznnike.demokmp.app.viewmodel.dbexample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.interactor.dbexample.AddDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteAllDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.GetDBExampleDataListUseCase
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

class DBExampleViewModel : BaseUiViewModel<DBExampleViewModel.UiState>() {
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val getDBExampleDataListUseCase: GetDBExampleDataListUseCase by inject()
    private val addDBExampleDataUseCase: AddDBExampleDataUseCase by inject()
    private val deleteDBExampleDataUseCase: DeleteDBExampleDataUseCase by inject()
    private val deleteAllDBExampleDataUseCase: DeleteAllDBExampleDataUseCase by inject()

    var nameInput by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            setProgress(true)
            loadData()
            setProgress(false)
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

    private fun setData(data: List<DBExampleData>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                data = data
            )
        }
    }

    private suspend fun loadData() {
        getDBExampleDataListUseCase().process(
            { result ->
                setData(result)
            }, ::onError
        )
    }

    private suspend fun onError(error: Exception) {
        setProgress(false)
        errorHandler.proceed(error) { message ->
            notifier.sendAlert(message)
        }
    }

    fun onNameInput(newValue: String) {
        nameInput = newValue
    }

    fun addData() {
        viewModelScope.launch {
            if (nameInput.isBlank() || mutableUiState.value.isLoading) return@launch

            setProgress(true)
            val data = DBExampleData(
                name = nameInput
            )
            addDBExampleDataUseCase(data).process(
                {
                    nameInput = ""
                }, ::onError
            )
            loadData()
            setProgress(false)
        }
    }

    fun deleteData(data: DBExampleData) {
        viewModelScope.launch {
            setProgress(true)
            deleteDBExampleDataUseCase(data).process(
                { }, ::onError
            )
            loadData()
            setProgress(false)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            setProgress(true)
            deleteAllDBExampleDataUseCase().process(
                { }, ::onError
            )
            loadData()
            setProgress(false)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val data: List<DBExampleData> = emptyList()
    )
}