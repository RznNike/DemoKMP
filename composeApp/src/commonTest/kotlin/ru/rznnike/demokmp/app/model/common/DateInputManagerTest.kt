package ru.rznnike.demokmp.app.model.common

import ru.rznnike.demokmp.domain.utils.toInputString
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val DEFAULT_DATE = LocalDate.of(2024, 5, 17)

private fun dateInputManager(
    date: LocalDate = DEFAULT_DATE,
    min: LocalDate = LocalDate.MIN,
    max: LocalDate = LocalDate.MAX
) = DateInputManager(
    defaultValue = date,
    dateMin = min,
    dateMax = max
)

class DateInputManagerTest {
    @Test
    fun `new manager exposes the default date`() {
        // Act
        val manager = dateInputManager()

        // Assert
        assertEquals(DEFAULT_DATE.toInputString(), manager.inputString)
        assertEquals(DEFAULT_DATE, manager.inputValue)
        assertFalse(manager.isError)
    }

    @Test
    fun `empty input is not an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("")

        // Assert
        assertEquals("", manager.inputString)
        assertFalse(manager.isError)
    }

    @Test
    fun `partial single digit day is not an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("1")

        // Assert
        assertFalse(manager.isError)
    }

    @Test
    fun `partial two digit day is not an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("31")

        // Assert
        assertFalse(manager.isError)
    }

    @Test
    fun `partial day and month is not an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("311")

        // Assert
        assertFalse(manager.isError)
    }

    @Test
    fun `day zero is an error once two digits are entered`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("00")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `day above 31 is an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("32")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `February 31 is an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("3102")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `February 29 is valid in a leap year`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("29022024")

        // Assert
        assertFalse(manager.isError)
    }

    @Test
    fun `February 29 is an error in a non leap year`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("29022023")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `month 13 is an error`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("0113")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `month 00 is an error once four digits are entered`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("0100")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `year below dateMin is an error`() {
        // Arrange
        val manager = dateInputManager(min = LocalDate.of(2020, 1, 1))

        // Act
        manager.setInput("01022019")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `date above dateMax is an error`() {
        // Arrange
        val manager = dateInputManager(max = LocalDate.of(2024, 12, 31))

        // Act
        manager.setInput("01012025")

        // Assert
        assertTrue(manager.isError)
    }

    @Test
    fun `date inside dateMin and dateMax is valid`() {
        // Arrange
        val manager = dateInputManager(
            min = LocalDate.of(2024, 1, 1),
            max = LocalDate.of(2024, 12, 31)
        )

        // Act
        manager.setInput("31012024")

        // Assert
        assertFalse(manager.isError)
    }

    @Test
    fun `setInput exposes the raw value`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.setInput("123")

        // Assert
        assertEquals("123", manager.inputString)
    }

    @Test
    fun `setDate stores the date as an input string`() {
        // Arrange
        val manager = dateInputManager()
        val date = LocalDate.of(2023, 12, 31)

        // Act
        manager.setDate(date)

        // Assert
        assertEquals("31122023", manager.inputString)
        assertEquals(date.toInputString(), manager.inputString)
    }

    @Test
    fun `confirmInput keeps the previous date for a short input`() {
        // Arrange
        val manager = dateInputManager()
        manager.setInput("31")

        // Act
        manager.confirmInput()

        // Assert
        assertEquals(DEFAULT_DATE.toInputString(), manager.inputString)
        assertEquals(DEFAULT_DATE, manager.inputValue)
    }

    @Test
    fun `confirmInput restores the previous date for an invalid input`() {
        // Arrange
        val manager = dateInputManager()
        manager.setInput("99012024")
        assertTrue(manager.isError)

        // Act
        manager.confirmInput()

        // Assert
        assertEquals(DEFAULT_DATE.toInputString(), manager.inputString)
        assertEquals(DEFAULT_DATE, manager.inputValue)
        assertFalse(manager.isError)
    }

    @Test
    fun `confirmInput applies a valid input`() {
        // Arrange
        val manager = dateInputManager()
        manager.setInput("01022024")

        // Act
        manager.confirmInput()

        // Assert
        assertEquals(LocalDate.of(2024, 2, 1), manager.inputValue)
        assertEquals("01022024", manager.inputString)
    }

    @Test
    fun `changeDate moves forward and backward within range`() {
        // Arrange
        val manager = dateInputManager()

        // Act
        manager.changeDate(1)

        // Assert
        assertEquals("18052024", manager.inputString)

        // Act
        manager.changeDate(-2)

        // Assert
        assertEquals("16052024", manager.inputString)
    }

    @Test
    fun `changeDate clamps at dateMin`() {
        // Arrange
        val manager = dateInputManager(
            date = LocalDate.of(2024, 1, 1),
            min = LocalDate.of(2024, 1, 1)
        )

        // Act
        manager.changeDate(-1)

        // Assert
        assertEquals("01012024", manager.inputString)
    }

    @Test
    fun `changeDate clamps at dateMax`() {
        // Arrange
        val manager = dateInputManager(
            date = LocalDate.of(2024, 12, 31),
            max = LocalDate.of(2024, 12, 31)
        )

        // Act
        manager.changeDate(1)

        // Assert
        assertEquals("31122024", manager.inputString)
    }

    @Test
    fun `changeDate uses the confirmed date for partial input`() {
        // Arrange
        val manager = dateInputManager()
        manager.setInput("31")

        // Act
        manager.changeDate(1)

        // Assert
        assertEquals("18052024", manager.inputString)
    }

    @Test
    fun `isError is cleared after the input is corrected`() {
        // Arrange
        val manager = dateInputManager()
        manager.setInput("3102")
        assertTrue(manager.isError)

        // Act
        manager.setInput("0102")

        // Assert
        assertFalse(manager.isError)
    }
}
