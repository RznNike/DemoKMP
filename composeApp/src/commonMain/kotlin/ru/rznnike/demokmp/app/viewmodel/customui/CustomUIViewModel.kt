package ru.rznnike.demokmp.app.viewmodel.customui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.inject
import ru.rznnike.demokmp.app.common.viewmodel.BaseUiViewModel
import ru.rznnike.demokmp.domain.utils.toInputString
import ru.rznnike.demokmp.domain.utils.toLocalDate
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.tab_1
import ru.rznnike.demokmp.generated.resources.tab_2
import ru.rznnike.demokmp.generated.resources.tab_3
import java.time.Clock
import java.time.LocalDate
import java.time.Month

private const val DATE_DAY_MAX = 31
private const val DATE_MONTH_MAX = 12
private const val DATE_YEAR_MIN = 2000

class CustomUIViewModel : BaseUiViewModel<CustomUIViewModel.UiState>() {
    private val clock: Clock by inject()

    var textInput by mutableStateOf("")
        private set
    var dateInput by mutableStateOf("")
        private set
    var dateError by mutableStateOf(false)
        private set

    override fun provideDefaultUIState(): UiState {
        val currentDate = clock.millis().toLocalDate()
        return UiState(
            date = currentDate,
            dateMin = LocalDate.of(DATE_YEAR_MIN, 1, 1),
            dateMax = currentDate
        )
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

    fun onDateInput(newValue: String) {
        dateInput = newValue
        dateError = validateDateInput(newValue)
    }

    private fun validateDateInput(dateString: String): Boolean {
        var isError = false

        var buffer = dateString
        val day = buffer.take(2).toIntOrNull()
        buffer = buffer.removeRange(0, minOf(2, buffer.length))
        val month = buffer.take(2).toIntOrNull()
        buffer = buffer.removeRange(0, minOf(2, buffer.length))
        val year = buffer.toIntOrNull()

        val currentDate = clock.millis().toLocalDate()
        day?.let {
            isError = (day > DATE_DAY_MAX)
                    || ((dateString.length >= 2) && (day < 1))
            month?.let {
                if ((1..DATE_MONTH_MAX).contains(month)) {
                    val lengthOfMonth = Month.of(month).maxLength()
                    isError = isError || (day > lengthOfMonth)
                    year?.let {
                        try {
                            val parsedDate = LocalDate.of(year, month, day)
                            isError = isError
                                    || ((dateString.length == 8) && (year < DATE_YEAR_MIN))
                                    || (parsedDate > currentDate)
                        } catch (e: Exception) {
                            isError = true
                        }
                    }
                } else {
                    isError = isError
                            || (month > DATE_MONTH_MAX)
                            || (dateString.length >= 4)
                }
            }
        }

        return isError
    }

    fun confirmDateInput() {
        if ((!dateError) && (dateInput.length > 4)) {
            setDate(parseDateInput(dateInput))
        }
        copyDateToDateInput()
    }

    private fun copyDateToDateInput() {
        dateInput = mutableUiState.value.date.toInputString()
        dateError = false
    }

    private fun setDate(newValue: LocalDate) {
        if (newValue == mutableUiState.value.date) return

        mutableUiState.update { currentState ->
            currentState.copy(
                date = newValue,
                dateMax = clock.millis().toLocalDate()
            )
        }
    }

    private fun parseDateInput(input: String): LocalDate {
        var buffer = input
        val day = buffer.take(2).toInt()
        buffer = buffer.removeRange(0, 2)
        val month = buffer.take(2).toInt()
        buffer = buffer.removeRange(0, 2)
        val year = buffer.toInt()

        return LocalDate.of(year, month, day)
    }

    fun onDateChange(delta: Int) {
        val currentDate = if (dateInput.length > 4) {
            parseDateInput(dateInput)
        } else {
            mutableUiState.value.date
        }
        val newDate = currentDate.plusDays(delta.toLong())
        if ((mutableUiState.value.dateMin..mutableUiState.value.dateMax).contains(newDate)) {
            setDate(newDate)
        }
        copyDateToDateInput()
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
        val date: LocalDate,
        val dateMin: LocalDate,
        val dateMax: LocalDate,
        val dropdownOptions: List<String> = (1..20).map {
            "Option $it"
        },
        val dropdownSelection: String = dropdownOptions.first(),
        val dropdownQuerySelection: String = dropdownOptions.first()
    )

    enum class Tab(
        val nameRes: StringResource
    ) {
        ONE(Res.string.tab_1),
        TWO(Res.string.tab_2),
        THREE(Res.string.tab_3)
    }
}