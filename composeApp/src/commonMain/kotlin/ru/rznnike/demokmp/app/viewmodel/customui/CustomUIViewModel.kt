package ru.rznnike.demokmp.app.viewmodel.customui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.app.model.common.DateInputManager
import ru.rznnike.demokmp.domain.utils.toLocalDate
import ru.rznnike.demokmp.generated.resources.*
import java.time.Clock
import java.time.LocalDate

private const val DATE_YEAR_MIN = 2000

class CustomUIViewModel : BaseUiViewModel<CustomUIViewModel.UiState>() {
    private val clock: Clock by inject()

    var textInput by mutableStateOf("")
        private set

    val dateInputManager = DateInputManager(
        defaultValue = clock.millis().toLocalDate()
    )

    init {
        dateInputManager.copyFilterToDateInput(mutableUiState.value.dateFilter)
        setupDropdownOptions()
    }

    override fun provideDefaultUIState() = UiState(
        dateFilter = getDefaultDateFilter()
    )

    private fun getDefaultDateFilter(): DateFilter {
        val currentDate = clock.millis().toLocalDate()
        return DateFilter(
            value = currentDate,
            dateMin = LocalDate.of(DATE_YEAR_MIN, 1, 1),
            dateMax = currentDate
        )
    }

    private fun DateInputManager.copyFilterToDateInput(filter: DateFilter) {
        dateMin = filter.dateMin
        dateMax = filter.dateMax
        setDate(filter.value)
    }

    fun confirmDateInput() {
        dateInputManager.confirmInput()
        updateDateFilter()
    }

    fun onDateChange(delta: Int) {
        dateInputManager.changeDate(delta)
        updateDateFilter()
    }

    private fun updateDateFilter() {
        if (dateInputManager.inputValue == mutableUiState.value.dateFilter.value) return

        mutableUiState.update { currentState ->
            currentState.copy(
                dateFilter = currentState.dateFilter.copy(
                    value = dateInputManager.inputValue,
                    dateMax = clock.millis().toLocalDate()
                )
            )
        }
        dateInputManager.copyFilterToDateInput(mutableUiState.value.dateFilter)
    }

    private fun setupDropdownOptions() {
        viewModelScope.launch {
            val name = getString(Res.string.option)
            val options = (1..20).map { "$name $it" }

            mutableUiState.update { currentState ->
                currentState.copy(
                    dropdownOptions = options,
                    dropdownSelection = options.first(),
                    dropdownQuerySelection = options.first()
                )
            }
        }
    }

    fun onTabChanged(newValue: Tab) {
        if (newValue == mutableUiState.value.selectedTab) return

        mutableUiState.update { currentState ->
            currentState.copy(
                selectedTab = newValue
            )
        }
    }

    fun onTextInput(newValue: String) {
        textInput = newValue
    }

    fun onDropdownSelectionChanged(newValue: String) {
        if (newValue == mutableUiState.value.dropdownSelection) return

        mutableUiState.update { currentState ->
            currentState.copy(
                dropdownSelection = newValue
            )
        }
    }

    fun onDropdownQuerySelectionChanged(newValue: String) {
        if (newValue == mutableUiState.value.dropdownSelection) return

        mutableUiState.update { currentState ->
            currentState.copy(
                dropdownQuerySelection = newValue
            )
        }
    }

    data class UiState(
        val selectedTab: Tab = Tab.ONE,
        val dateFilter: DateFilter,
        val dropdownOptions: List<String> = listOf(),
        val dropdownSelection: String = "",
        val dropdownQuerySelection: String = ""
    )

    enum class Tab(
        val nameRes: StringResource
    ) {
        ONE(Res.string.tab_1),
        TWO(Res.string.tab_2),
        THREE(Res.string.tab_3)
    }

    data class DateFilter(
        val value: LocalDate,
        val dateMin: LocalDate,
        val dateMax: LocalDate
    )
}