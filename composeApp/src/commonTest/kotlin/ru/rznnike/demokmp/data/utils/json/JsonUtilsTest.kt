package ru.rznnike.demokmp.data.utils.json

import kotlinx.serialization.json.Json
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonUtilsTest {
    @Test
    fun `prettifyJson preserves json content`() {
        // Arrange
        val input = """{"name":"value","count":2}"""

        // Act
        val result = input.prettifyJson()

        // Assert
        assertEquals(Json.parseToJsonElement(input), Json.parseToJsonElement(result))
    }

    @Test
    fun `prettifyJson splits valid json into multiple lines`() {
        // Act
        val result = """{"name":"value","count":2}""".prettifyJson()

        // Assert
        assertTrue(result.lines().size > 1)
    }

    @Test
    fun `prettifyJson returns original text when json is invalid`() {
        // Act
        val result = "not json".prettifyJson()

        // Assert
        assertEquals("not json", result)
    }

    @Test
    fun `safeDecode decodes valid json`() {
        // Act
        val result = """["a","b"]""".safeDecode<List<String>>()

        // Assert
        assertEquals(listOf("a", "b"), result)
    }

    @Test
    fun `safeDecode maps serial names to properties`() {
        // Act
        val result = """{"message":["https://example.com/1.jpg"]}""".safeDecode<RandomImageLinksModel>()

        // Assert
        assertEquals(RandomImageLinksModel(links = listOf("https://example.com/1.jpg")), result)
    }

    @Test
    fun `safeDecode returns null when json is invalid`() {
        // Act
        val result = "not json".safeDecode<List<String>>()

        // Assert
        assertNull(result)
    }

    @Test
    fun `safeDecode returns null for null receiver`() {
        // Arrange
        val input: String? = null

        // Act
        val result = input.safeDecode<List<String>>()

        // Assert
        assertNull(result)
    }
}
