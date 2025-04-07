package ru.rznnike.demokmp.app.model.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.rznnike.demokmp.domain.utils.toInputString
import java.time.LocalDate
import java.time.Month

private const val DATE_DAY_MAX = 31
private const val DATE_MONTH_MAX = 12

class DateInputManager(
    defaultValue: LocalDate,
    var dateMin: LocalDate = LocalDate.MIN,
    var dateMax: LocalDate = LocalDate.MAX
) {
    var inputString by mutableStateOf("")
        private set
    var isError by mutableStateOf(false)
        private set

    var inputValue: LocalDate = defaultValue
        private set

    init {
        setDate(defaultValue)
    }

    fun setInput(newInput: String) {
        inputString = newInput
        isError = validateDateInput()
    }

    fun confirmInput() {
        if ((!isError) && (inputString.length > 4)) {
            inputValue = parseDateInput()
        } else {
            setInput(inputValue.toInputString())
        }
    }

    fun setDate(newDate: LocalDate) {
        setInput(newDate.toInputString())
    }

    fun changeDate(delta: Int) {
        val currentDate = if (inputString.length > 4) parseDateInput() else inputValue
        val newDate = currentDate.plusDays(delta.toLong())
        if ((dateMin..dateMax).contains(newDate)) {
            setDate(newDate)
        }
    }

    private fun validateDateInput(): Boolean {
        var isError = false

        var buffer = inputString
        val day = buffer.take(2).toIntOrNull()
        buffer = buffer.drop(2)
        val month = buffer.take(2).toIntOrNull()
        buffer = buffer.drop(2)
        val year = buffer.toIntOrNull()

        day?.let {
            isError = (day > DATE_DAY_MAX) || ((inputString.length >= 2) && (day < 1))
            month?.let {
                if ((1..DATE_MONTH_MAX).contains(month)) {
                    val lengthOfMonth = Month.of(month).maxLength()
                    isError = isError || (day > lengthOfMonth)
                    year?.let {
                        try {
                            val parsedDate = LocalDate.of(year, month, day)
                            isError = isError
                                    || ((inputString.length == 8) && (year < dateMin.year))
                                    || (!(dateMin..dateMax).contains(parsedDate))
                        } catch (e: Exception) {
                            isError = true
                        }
                    }
                } else {
                    isError = isError || (month > DATE_MONTH_MAX) || (inputString.length >= 4)
                }
            }
        }

        return isError
    }

    private fun parseDateInput(): LocalDate {
        return if (inputString.length > 4) {
            val day = inputString.substring(0, 2).toInt()
            val month = inputString.substring(2, 4).toInt()
            val year = inputString.substring(4).toInt()
            LocalDate.of(year, month, day)
        } else {
            inputValue
        }
    }
}