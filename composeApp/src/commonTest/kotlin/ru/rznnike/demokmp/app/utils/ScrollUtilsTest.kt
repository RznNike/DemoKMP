package ru.rznnike.demokmp.app.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val VIEWPORT_HEIGHT = 1_000

class ScrollUtilsTest {
    @Test
    fun `computeScrollTarget returns no target for an empty list`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 5,
            totalItemsCount = 0,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 0, offset = 0, size = 100),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `computeScrollTarget clamps a negative index to the first item`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = -5,
            totalItemsCount = 10,
            firstVisibleItemIndex = 7,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 100),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 0, offset = 0), result)
    }

    @Test
    fun `computeScrollTarget clamps an index past the end to the last item`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 999,
            totalItemsCount = 10,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 0, offset = 0, size = 100),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 9, offset = -900), result)
    }

    @Test
    fun `computeScrollTarget scrolls without offset when the target is above the first visible item`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 3,
            totalItemsCount = 100,
            firstVisibleItemIndex = 5,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 200),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 3, offset = 0), result)
    }

    @Test
    fun `computeScrollTarget returns no target when the target is the first visible item`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 5,
            totalItemsCount = 100,
            firstVisibleItemIndex = 5,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 200),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `computeScrollTarget returns no target when the target is the last visible item`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 9,
            totalItemsCount = 100,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 200),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `computeScrollTarget uses a negative offset when the last visible item fits the viewport`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 20,
            totalItemsCount = 100,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 200),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 20, offset = -800), result)
    }

    @Test
    fun `computeScrollTarget uses a positive offset when the last visible item overflows the viewport`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 12,
            totalItemsCount = 100,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 9, offset = 0, size = 2_500),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 12, offset = 1_500), result)
    }

    @Test
    fun `computeScrollTarget counts a partially visible last item as visible`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 9,
            totalItemsCount = 100,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 9, offset = 500, size = 500),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 9, offset = -500), result)
    }

    @Test
    fun `computeScrollTarget scrolls to the item when nothing is visible yet`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 5,
            totalItemsCount = 100,
            firstVisibleItemIndex = 0,
            lastVisibleItem = null,
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertEquals(ScrollTarget(index = 5, offset = -1_000), result)
    }

    @Test
    fun `computeScrollTarget returns no target for a single item list`() {
        // Act
        val result = computeScrollTarget(
            targetIndex = 0,
            totalItemsCount = 1,
            firstVisibleItemIndex = 0,
            lastVisibleItem = VisibleItem(index = 0, offset = 0, size = 200),
            viewportHeight = VIEWPORT_HEIGHT,
        )

        // Assert
        assertNull(result)
    }
}
