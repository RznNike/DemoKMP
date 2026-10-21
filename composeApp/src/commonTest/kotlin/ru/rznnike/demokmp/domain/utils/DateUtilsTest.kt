package ru.rznnike.demokmp.domain.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SAMPLE_MILLIS = 1_700_000_000_000L

private fun startOfDayMillis(date: LocalDate): Long =
    date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun zonedMillis(dateTime: LocalDateTime): Long =
    ZonedDateTime.of(dateTime, ZoneId.systemDefault()).toInstant().toEpochMilli()

class DateUtilsTest {
    @Test
    fun `Long toDateString formats epoch in UTC when zeroTimeZone is true`() {
        // Act
        val result = 0L.toDateString(zeroTimeZone = true)

        // Assert
        assertEquals("01.01.1970", result)
    }

    @Test
    fun `Long toDateString formats time with milliseconds in UTC`() {
        // Act
        val result = SAMPLE_MILLIS.toDateString(
            pattern = GlobalConstants.DATE_PATTERN_TIME_MS,
            zeroTimeZone = true
        )

        // Assert
        assertEquals("22:13:20.000", result)
    }

    @Test
    fun `Long toDateString round-trips through String toTimeStamp`() {
        // Arrange
        val expected = zonedMillis(LocalDateTime.of(2024, 1, 2, 3, 4))

        // Act
        val result = expected.toDateString(GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME).toTimeStamp()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `String toTimeStamp returns zero for empty input`() {
        // Act
        val result = "".toTimeStamp()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `String toTimeStamp parses date and time in the system time zone`() {
        // Arrange
        val expected = zonedMillis(LocalDateTime.of(2024, 1, 1, 10, 30))

        // Act
        val result = "01.01.2024 10:30".toTimeStamp()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `String toTimeStamp returns zero for date-only input when the pattern requires time`() {
        // Act
        val result = "01.01.2024".toTimeStamp()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `String toTimeStamp returns zero for unparseable input`() {
        // Act
        val result = "not a date".toTimeStamp()

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `String toTimeStamp honors a custom pattern`() {
        // Arrange
        val expected = startOfDayMillis(LocalDate.of(2024, 3, 15))

        // Act
        val result = "2024.03.15".toTimeStamp(GlobalConstants.DATE_PATTERN_FILE_NAME_DAY)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `Long toDateTime uses the system time zone`() {
        // Act
        val result = SAMPLE_MILLIS.toDateTime()

        // Assert
        assertEquals(ZoneId.systemDefault(), result.zone)
        assertEquals(SAMPLE_MILLIS, result.toInstant().toEpochMilli())
    }

    @Test
    fun `Long toLocalDate returns the local date of the instant`() {
        // Arrange
        val date = LocalDate.of(2024, 3, 15)

        // Act
        val result = startOfDayMillis(date).toLocalDate()

        // Assert
        assertEquals(date, result)
    }

    @Test
    fun `Long toLocalDateTime returns the local date time of the instant`() {
        // Arrange
        val dateTime = LocalDateTime.of(2024, 3, 15, 10, 30)

        // Act
        val result = zonedMillis(dateTime).toLocalDateTime()

        // Assert
        assertEquals(dateTime, result)
    }

    @Test
    fun `ZonedDateTime millis returns epoch milliseconds`() {
        // Arrange
        val zoned = ZonedDateTime.of(LocalDateTime.of(2024, 1, 1, 12, 0), ZoneOffset.UTC)

        // Act
        val result = zoned.millis()

        // Assert
        assertEquals(1_704_110_400_000L, result)
    }

    @Test
    fun `LocalDateTime millis uses the system time zone`() {
        // Arrange
        val dateTime = LocalDateTime.of(2024, 6, 15, 12, 0)

        // Act
        val result = dateTime.millis()

        // Assert
        assertEquals(zonedMillis(dateTime), result)
    }

    @Test
    fun `LocalDate millis returns start of day in the system time zone`() {
        // Arrange
        val date = LocalDate.of(2024, 6, 15)

        // Act
        val result = date.millis()

        // Assert
        assertEquals(startOfDayMillis(date), result)
    }

    @Test
    fun `LocalDate utcMillis returns start of day in UTC`() {
        // Act
        val result = LocalDate.of(2024, 1, 1).utcMillis()

        // Assert
        assertEquals(1_704_067_200_000L, result)
    }

    @Test
    fun `LocalDate atEndOfDay returns the last nanosecond of the day`() {
        // Act
        val result = LocalDate.of(2024, 1, 31).atEndOfDay()

        // Assert
        assertEquals(LocalDateTime.of(2024, 1, 31, 23, 59, 59, 999_999_999), result)
    }

    @Test
    fun `LocalDate atStartOfMonth returns the first day`() {
        // Act
        val result = LocalDate.of(2024, 2, 29).atStartOfMonth()

        // Assert
        assertEquals(LocalDate.of(2024, 2, 1), result)
    }

    @Test
    fun `LocalDate atEndOfMonth returns February 29 in a leap year`() {
        // Act
        val result = LocalDate.of(2024, 2, 1).atEndOfMonth()

        // Assert
        assertEquals(LocalDate.of(2024, 2, 29), result)
    }

    @Test
    fun `LocalDate atEndOfMonth returns February 28 in a non-leap year`() {
        // Act
        val result = LocalDate.of(2023, 2, 1).atEndOfMonth()

        // Assert
        assertEquals(LocalDate.of(2023, 2, 28), result)
    }

    @Test
    fun `LocalDate atEndOfMonth handles 30 and 31 day months`() {
        // Act
        val endOfApril = LocalDate.of(2024, 4, 10).atEndOfMonth()
        val endOfJuly = LocalDate.of(2024, 7, 10).atEndOfMonth()

        // Assert
        assertEquals(LocalDate.of(2024, 4, 30), endOfApril)
        assertEquals(LocalDate.of(2024, 7, 31), endOfJuly)
    }

    @Test
    fun `YearMonth atStartOfMonth returns the first day`() {
        // Act
        val result = YearMonth.of(2024, 2).atStartOfMonth()

        // Assert
        assertEquals(LocalDate.of(2024, 2, 1), result)
    }

    @Test
    fun `YearMonth toDateString formats the first day of the month`() {
        // Act
        val result = YearMonth.of(2024, 2).toDateString(GlobalConstants.DATE_PATTERN_SIMPLE)

        // Assert
        assertEquals("01.02.2024", result)
    }

    @Test
    fun `YearMonth toDateString uses the localized month name by default`() {
        // Arrange
        val monthName = Month.FEBRUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())

        // Act
        val result = YearMonth.of(2024, 2).toDateString()

        // Assert
        assertEquals("$monthName 2024", result)
    }

    @Test
    fun `LocalDate toDateString formats zero padded day month year`() {
        // Act
        val result = LocalDate.of(2024, 3, 5).toDateString()

        // Assert
        assertEquals("05.03.2024", result)
    }

    @Test
    fun `LocalDate toInputString formats zero padded digits`() {
        // Act
        val result = LocalDate.of(2024, 3, 5).toInputString()

        // Assert
        assertEquals("05032024", result)
    }

    @Test
    fun `currentTimeMillis returns the current wall clock time`() {
        // Arrange
        val before = System.currentTimeMillis()

        // Act
        val result = currentTimeMillis()

        // Assert
        val after = System.currentTimeMillis()
        assertTrue(result in before..after)
    }
}
