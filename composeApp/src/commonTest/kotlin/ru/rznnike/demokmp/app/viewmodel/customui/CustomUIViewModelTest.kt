package ru.rznnike.demokmp.app.viewmodel.customui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.StringResource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.rznnike.demokmp.app.common.viewmodel.ViewModelTestRule
import ru.rznnike.demokmp.app.utils.MutableClock
import ru.rznnike.demokmp.domain.utils.millis
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.option
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

private val DEFAULT_DATE = LocalDate.of(2024, 5, 17)
private val DEFAULT_DATE_MIN = LocalDate.of(2000, 1, 1)
private const val OPTION_NAME = "Option"

/**
 * The VM reads the clock from Koin, so time is driven by [MutableClock].
 *
 * `getString(Res.string.…)` does not resolve in `commonTest`, hence the injected `stringRetriever`.
 *
 * Date contract pinned here: the spinner / typed input only moves `DateInputManager.inputValue`
 * preview state; `DateFilter` is committed by `confirmDateInput()` (tabulation, Enter or calendar pick in
 * `DateTextField`).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CustomUIViewModelTest {
    private val rule = ViewModelTestRule()
    private val mutableClock = MutableClock(Instant.ofEpochMilli(DEFAULT_DATE.millis()))
    private val retrievedResources = mutableListOf<StringResource>()

    private fun viewModel() = CustomUIViewModel(
        stringRetriever = { resource ->
            retrievedResources += resource
            OPTION_NAME
        }
    )

    @BeforeTest
    fun setUp() {
        rule.start()
        startKoin {
            modules(
                module {
                    single<Clock> { mutableClock }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        rule.stop()
    }

    @Test
    fun `default date filter starts at today and is bounded by 2000-01-01`() = runTest(rule.dispatcher) {
        // Act
        val viewModel = viewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(
            CustomUIViewModel.DateFilter(
                value = DEFAULT_DATE,
                dateMin = DEFAULT_DATE_MIN,
                dateMax = DEFAULT_DATE
            ),
            viewModel.uiState.value.dateFilter
        )
    }

    @Test
    fun `date input manager is initialised from the default filter`() = runTest(rule.dispatcher) {
        // Act
        val viewModel = viewModel()
        advanceUntilIdle()

        // Assert
        assertEquals("17052024", viewModel.dateInputManager.inputString)
        assertEquals(DEFAULT_DATE, viewModel.dateInputManager.inputValue)
        assertEquals(DEFAULT_DATE_MIN, viewModel.dateInputManager.dateMin)
        assertEquals(DEFAULT_DATE, viewModel.dateInputManager.dateMax)
    }

    @Test
    fun `spinner only moves the input preview, not the committed filter`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()

        // Act
        viewModel.onDateChange(-1)

        // Assert
        assertEquals("16052024", viewModel.dateInputManager.inputString)
        assertEquals(DEFAULT_DATE, viewModel.dateInputManager.inputValue, "inputValue is only set on confirm")
        assertEquals(DEFAULT_DATE, viewModel.uiState.value.dateFilter.value)
    }

    @Test
    fun `confirming after a spin propagates the date to the filter`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onDateChange(-1)

        // Act
        viewModel.confirmDateInput()

        // Assert
        assertEquals(LocalDate.of(2024, 5, 16), viewModel.uiState.value.dateFilter.value)
        assertEquals(LocalDate.of(2024, 5, 16), viewModel.dateInputManager.inputValue)
        assertEquals("16052024", viewModel.dateInputManager.inputString)
    }

    @Test
    fun `confirming refreshes dateMax from the clock`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onDateChange(-1)
        mutableClock.current = Instant.ofEpochMilli(LocalDate.of(2024, 5, 20).millis())

        // Act
        viewModel.confirmDateInput()

        // Assert
        assertEquals(LocalDate.of(2024, 5, 20), viewModel.uiState.value.dateFilter.dateMax)
        assertEquals(LocalDate.of(2024, 5, 20), viewModel.dateInputManager.dateMax)
        assertEquals(LocalDate.of(2024, 5, 16), viewModel.uiState.value.dateFilter.value)
    }

    @Test
    fun `spinner cannot move past dateMax`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()

        // Act (today is already the upper bound)
        viewModel.onDateChange(1)

        // Assert
        assertEquals("17052024", viewModel.dateInputManager.inputString)
        assertEquals(DEFAULT_DATE, viewModel.uiState.value.dateFilter.value)
    }

    @Test
    fun `confirm without a change keeps the filter`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        val dateFilter = viewModel.uiState.value.dateFilter

        // Act
        viewModel.confirmDateInput()

        // Assert
        assertEquals(dateFilter, viewModel.uiState.value.dateFilter)
        assertEquals("17052024", viewModel.dateInputManager.inputString)
    }

    @Test
    fun `typed date is committed on confirm`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.dateInputManager.setInput("01022024")

        // Act
        viewModel.confirmDateInput()

        // Assert
        assertEquals(LocalDate.of(2024, 2, 1), viewModel.uiState.value.dateFilter.value)
        assertEquals(LocalDate.of(2024, 2, 1), viewModel.dateInputManager.inputValue)
    }

    @Test
    fun `invalid typed date is reverted on confirm`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.dateInputManager.setInput("99012024")

        // Act
        viewModel.confirmDateInput()

        // Assert
        assertEquals("17052024", viewModel.dateInputManager.inputString)
        assertEquals(DEFAULT_DATE, viewModel.dateInputManager.inputValue)
        assertEquals(DEFAULT_DATE, viewModel.uiState.value.dateFilter.value)
    }

    @Test
    fun `dropdown is filled with 20 options for the requested resource`() = runTest(rule.dispatcher) {
        // Act
        val viewModel = viewModel()
        advanceUntilIdle()

        // Assert
        assertEquals((1..20).map { "$OPTION_NAME $it" }, viewModel.uiState.value.dropdownOptions)
        assertEquals("$OPTION_NAME 1", viewModel.uiState.value.dropdownSelection)
        assertEquals("$OPTION_NAME 1", viewModel.uiState.value.dropdownQuerySelection)
        assertEquals(listOf(Res.string.option), retrievedResources)
    }

    @Test
    fun `dropdown selection is updated and the same value is ignored`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        val querySelection = viewModel.uiState.value.dropdownQuerySelection

        // Act
        viewModel.onDropdownSelectionChanged("$OPTION_NAME 5")

        // Assert
        assertEquals("$OPTION_NAME 5", viewModel.uiState.value.dropdownSelection)
        assertEquals(querySelection, viewModel.uiState.value.dropdownQuerySelection)

        // Act (already selected)
        val state = viewModel.uiState.value
        viewModel.onDropdownSelectionChanged("$OPTION_NAME 5")

        // Assert
        assertSame(state, viewModel.uiState.value)
    }

    @Test
    fun `filtered dropdown selection is updated and the same value is ignored`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        val selection = viewModel.uiState.value.dropdownSelection

        // Act
        viewModel.onDropdownQuerySelectionChanged("$OPTION_NAME 7")

        // Assert
        assertEquals("$OPTION_NAME 7", viewModel.uiState.value.dropdownQuerySelection)
        assertEquals(selection, viewModel.uiState.value.dropdownSelection)

        // Act (already selected)
        val state = viewModel.uiState.value
        viewModel.onDropdownQuerySelectionChanged("$OPTION_NAME 7")

        // Assert
        assertSame(state, viewModel.uiState.value)
    }

    @Test
    fun `tab change is applied and the same tab is ignored`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(CustomUIViewModel.Tab.ONE, viewModel.uiState.value.selectedTab)

        // Act
        viewModel.onTabChanged(CustomUIViewModel.Tab.THREE)

        // Assert
        assertEquals(CustomUIViewModel.Tab.THREE, viewModel.uiState.value.selectedTab)

        // Act (already selected)
        val state = viewModel.uiState.value
        viewModel.onTabChanged(CustomUIViewModel.Tab.THREE)

        // Assert
        assertSame(state, viewModel.uiState.value)
    }

    @Test
    fun `text input is stored`() = runTest(rule.dispatcher) {
        // Arrange
        val viewModel = viewModel()
        advanceUntilIdle()

        // Act
        viewModel.onTextInput("DemoKMP")

        // Assert
        assertEquals("DemoKMP", viewModel.textInput)
    }
}
