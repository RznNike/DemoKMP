package ru.rznnike.demokmp.app.utils

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

private const val THRESHOLD_MS = 1_000L

class ClicksFilterTest {
    private val clock = MutableClock(Instant.ofEpochMilli(1_000_000L))
    private val filter = ClicksFilter(clock, THRESHOLD_MS)
    private var clicks = 0
    private val onClick: () -> Unit = { clicks += 1 }

    @Test
    fun `first click passes`() {
        // Act
        filter.filter(onClick)

        // Assert
        assertEquals(1, clicks)
    }

    @Test
    fun `click within the threshold is filtered`() {
        // Arrange
        filter.filter(onClick)

        // Act
        clock.advanceByMillis(THRESHOLD_MS - 1)
        filter.filter(onClick)

        // Assert
        assertEquals(1, clicks)
    }

    @Test
    fun `click exactly at the threshold is filtered`() {
        // Arrange
        filter.filter(onClick)

        // Act
        clock.advanceByMillis(THRESHOLD_MS)
        filter.filter(onClick)

        // Assert
        assertEquals(1, clicks)
    }

    @Test
    fun `click after the threshold passes`() {
        // Arrange
        filter.filter(onClick)

        // Act
        clock.advanceByMillis(THRESHOLD_MS + 1)
        filter.filter(onClick)

        // Assert
        assertEquals(2, clicks)
    }

    @Test
    fun `filtered click does not extend the threshold window`() {
        // Arrange
        filter.filter(onClick)
        clock.advanceByMillis(THRESHOLD_MS - 1)
        filter.filter(onClick)

        // Act
        clock.advanceByMillis(2)
        filter.filter(onClick)

        // Assert
        assertEquals(2, clicks)
    }
}
