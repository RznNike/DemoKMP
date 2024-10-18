package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.domain.utils.toInputString
import ru.rznnike.demokmp.domain.utils.toLocalDate
import ru.rznnike.demokmp.domain.utils.utcMillis
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_calendar
import java.time.LocalDate

private val INPUT_FILTER_REGEX = Regex("\\d*")
private const val MAX_LENGTH = 8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    labelRes: StringResource,
    value: String,
    isError: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    calendarInitialDate: LocalDate,
    calendarMinDate: LocalDate,
    calendarMaxDate: LocalDate,
    onValueChange: (String) -> Unit,
    onDateChange: (Int) -> Unit,
    onSave: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onFocusChanged {
                if (!it.isFocused) onSave()
            }
    ) {
        val focusManager = LocalFocusManager.current
        SlimOutlinedTextField(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { keyEvent ->
                    when {
                        (keyEvent.key == Key.DirectionUp) && (keyEvent.type == KeyEventType.KeyDown) -> {
                            onDateChange(1)
                            true
                        }
                        (keyEvent.key == Key.DirectionDown) && (keyEvent.type == KeyEventType.KeyDown) -> {
                            onDateChange(-1)
                            true
                        }
                        else -> false
                    }
                }
                .onEnterKey {
                    onSave()
                    focusManager.moveFocus(FocusDirection.Next)
                },
            value = value,
            singleLine = true,
            label = {
                TextR(labelRes)
            },
            placeholder = {
                Text(calendarInitialDate.toDateString())
            },
            onValueChange = {
                if (it.matches(INPUT_FILTER_REGEX) && it.length <= MAX_LENGTH) onValueChange(it)
            },
            visualTransformation = DateVisualTransformation(),
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .focusProperties {
                            canFocus = false
                        },
                    onClick = {
                        showDatePicker = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_calendar),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null
                    )
                }
            },
            isError = isError,
            contentPadding = contentPadding
        )

        if (showDatePicker) {
            val initDateUtc = calendarInitialDate.utcMillis()
            val minDateUtc = calendarMinDate.utcMillis()
            val maxDateUtc = calendarMaxDate.utcMillis()

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initDateUtc,
                yearRange = calendarMinDate.year..calendarMaxDate.year,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return (utcTimeMillis >= minDateUtc) && (utcTimeMillis <= maxDateUtc)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(y = 8.dp)
            ) {
                Popup(
                    properties = PopupProperties(
                        focusable = true
                    ),
                    onDismissRequest = { showDatePicker = false },
                    alignment = Alignment.TopStart
                ) {
                    Surface(
                        modifier = Modifier.padding(end = 16.dp),
                        shadowElevation = 4.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentSize provides 48.dp
                        ) {
                            DatePicker(
                                modifier = Modifier
                                    .width(400.dp)
                                    .padding(bottom = 16.dp),
                                state = datePickerState,
                                title = null,
                                headline = null,
                                showModeToggle = false,
                                colors = DatePickerDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    dividerColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

            val selectedDate = datePickerState.selectedDateMillis
            var isInitialSelectionSkipped by remember { mutableStateOf(false) }
            LaunchedEffect(selectedDate) {
                if (isInitialSelectionSkipped) {
                    selectedDate?.let {
                        val formattedDate = selectedDate.toLocalDate().toInputString()
                        onValueChange(formattedDate)
                        onSave()
                        showDatePicker = false
                    }
                } else {
                    isInitialSelectionSkipped = true
                }
            }
        }
    }
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val dotIndexesWithOffset = listOf(2, 5) // 00.00.0000
        val dotIndexes = dotIndexesWithOffset.mapIndexed { index, value -> value - index }

        val result = StringBuilder(text)
        dotIndexesWithOffset.forEach { dotIndex ->
            if ((result.lastIndex + 1) >= dotIndex) {
                result.insert(dotIndex, ".")
            }
        }
        return TransformedText(
            text = AnnotatedString(result.toString()),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val dotsCount = dotIndexes.count { it <= offset }
                    return offset + dotsCount
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val dotsCount = dotIndexesWithOffset.count { it <= offset }
                    return offset - dotsCount
                }
            }
        )
    }
}