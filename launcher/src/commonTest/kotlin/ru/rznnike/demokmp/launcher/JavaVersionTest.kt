package ru.rznnike.demokmp.launcher

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JavaVersionTest {
    @Test
    fun `parseJavaMajorVersion parses legacy 1 dot 8 version`() {
        val output = "java version \"1.8.0_333\"\nJava(TM) SE Runtime Environment"

        assertEquals(8, parseJavaMajorVersion(output))
    }

    @Test
    fun `parseJavaMajorVersion parses modern version`() {
        val output = "openjdk version \"17.0.2\" 2022-01-18"

        assertEquals(17, parseJavaMajorVersion(output))
    }

    @Test
    fun `parseJavaMajorVersion parses version without patch`() {
        val output = "openjdk version \"21\" 2023-09-19"

        assertEquals(21, parseJavaMajorVersion(output))
    }

    @Test
    fun `parseJavaMajorVersion parses unquoted version`() {
        val output = "java version 22.0.2 2024-07-16"

        assertEquals(22, parseJavaMajorVersion(output))
    }

    @Test
    fun `parseJavaMajorVersion parses early access version`() {
        val output = "openjdk version \"17-ea\" 2021-09-14"

        assertEquals(17, parseJavaMajorVersion(output))
    }

    @Test
    fun `parseJavaMajorVersion returns null when version is absent`() {
        assertNull(parseJavaMajorVersion("java: command not found"))
    }

    @Test
    fun `parseJavaMajorVersion returns null for empty output`() {
        assertNull(parseJavaMajorVersion(""))
    }
}
