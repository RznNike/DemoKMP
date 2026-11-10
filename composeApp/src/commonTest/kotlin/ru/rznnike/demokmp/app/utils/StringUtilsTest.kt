package ru.rznnike.demokmp.app.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringUtilsTest {
    @Test
    fun `formatAppVersion omits the build type for a release build`() {
        // Act
        val result = formatAppVersion(versionName = "1.0.0", versionCode = 1, buildType = "release")

        // Assert
        assertEquals("v 1.0.0.1", result)
    }

    @Test
    fun `formatAppVersion appends the build type for a debug build`() {
        // Act
        val result = formatAppVersion(versionName = "1.0.0", versionCode = 1, buildType = "debug")

        // Assert
        assertEquals("v 1.0.0.1 debug", result)
    }

    @Test
    fun `formatAppVersion appends the build type for a staging build`() {
        // Act
        val result = formatAppVersion(versionName = "1.0.0", versionCode = 1, buildType = "staging")

        // Assert
        assertEquals("v 1.0.0.1 staging", result)
    }

    @Test
    fun `formatAppVersion keeps multi digit version components`() {
        // Act
        val result = formatAppVersion(versionName = "2.3.11", versionCode = 42, buildType = "release")

        // Assert
        assertEquals("v 2.3.11.42", result)
    }

    @Test
    fun `findSubstringRanges returns the range of a single exact match`() {
        // Act
        val result = findSubstringRanges(text = "hello world", substring = "world")

        // Assert
        assertEquals(listOf(6..11), result)
    }

    @Test
    fun `findSubstringRanges ignores case`() {
        // Act
        val result = findSubstringRanges(text = "Hello World", substring = "wOrLd")

        // Assert
        assertEquals(listOf(6..11), result)
    }

    @Test
    fun `findSubstringRanges applies Unicode case folding`() {
        // Act
        val result = findSubstringRanges(text = "ÄÖÜ", substring = "öü")

        // Assert
        assertEquals(listOf(1..3), result)
    }

    @Test
    fun `findSubstringRanges returns every occurrence`() {
        // Act
        val result = findSubstringRanges(text = "abcabc", substring = "abc")

        // Assert
        assertEquals(listOf(0..3, 3..6), result)
    }

    @Test
    fun `findSubstringRanges does not return overlapping matches`() {
        // Act
        val result = findSubstringRanges(text = "aaaa", substring = "aa")

        // Assert
        assertEquals(listOf(0..2, 2..4), result)
    }

    @Test
    fun `findSubstringRanges treats regex wildcards literally`() {
        // Act
        val result = findSubstringRanges(text = "match .* here", substring = ".*")

        // Assert
        assertEquals(listOf(6..8), result)
    }

    @Test
    fun `findSubstringRanges treats a character class literally`() {
        // Act
        val result = findSubstringRanges(text = "text [a-z] end", substring = "[a-z]")

        // Assert
        assertEquals(listOf(5..10), result)
    }

    @Test
    fun `findSubstringRanges treats a backslash literally`() {
        // Act
        val result = findSubstringRanges(text = "a\\b", substring = "\\")

        // Assert
        assertEquals(listOf(1..2), result)
    }

    @Test
    fun `findSubstringRanges finds a match across lines`() {
        // Act
        val result = findSubstringRanges(text = "line1\nline2", substring = "LINE2")

        // Assert
        assertEquals(listOf(6..11), result)
    }

    @Test
    fun `findSubstringRanges returns the whole text when the substring equals it`() {
        // Act
        val result = findSubstringRanges(text = "abc", substring = "ABC")

        // Assert
        assertEquals(listOf(0..3), result)
    }

    @Test
    fun `findSubstringRanges returns nothing when the substring is absent`() {
        // Act
        val result = findSubstringRanges(text = "hello", substring = "xyz")

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findSubstringRanges returns nothing for an empty substring`() {
        // Act
        val result = findSubstringRanges(text = "hello", substring = "")

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findSubstringRanges returns nothing when the substring is longer than the text`() {
        // Act
        val result = findSubstringRanges(text = "hi", substring = "hello")

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findSubstringRanges returns nothing for empty text`() {
        // Act
        val result = findSubstringRanges(text = "", substring = "a")

        // Assert
        assertTrue(result.isEmpty())
    }
}
