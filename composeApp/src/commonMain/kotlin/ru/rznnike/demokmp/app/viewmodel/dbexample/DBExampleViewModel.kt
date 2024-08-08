package ru.rznnike.demokmp.app.viewmodel.dbexample

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.notifier.Notifier
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

    init {
        viewModelScope.launch(dispatcherProvider.default) {
            setProgress(true)
            loadData()
        }
    }

    override fun provideDefaultUIState() = UiState()

    private fun setProgress(isLoading: Boolean) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }

    private fun setData(data: List<DBExampleData>) {
        mutableUiState.update { currentState ->
            currentState.copy(
                isLoading = false,
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
        if (newValue != mutableUiState.value.nameInput) {
            mutableUiState.update { currentState ->
                currentState.copy(
                    nameInput = newValue
                )
            }
        }
    }

    fun addData() {
        viewModelScope.launch {
            setProgress(true)
            val data = DBExampleData(
                name = mutableUiState.value.nameInput
            )
            addDBExampleDataUseCase(data).process(
                {
                    mutableUiState.update { currentState ->
                        currentState.copy(
                            nameInput = ""
                        )
                    }
                }, ::onError
            )
            loadData()
        }
    }

    fun deleteData(data: DBExampleData) {
        viewModelScope.launch {
            setProgress(true)
            deleteDBExampleDataUseCase(data).process(
                { }, ::onError
            )
            loadData()
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            setProgress(true)
            deleteAllDBExampleDataUseCase().process(
                { }, ::onError
            )
            loadData()
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val data: List<DBExampleData> = emptyList(),
        val nameInput: String = ""
    )
}