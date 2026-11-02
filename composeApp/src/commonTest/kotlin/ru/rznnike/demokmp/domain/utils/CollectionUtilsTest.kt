package ru.rznnike.demokmp.domain.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data class Person(
    val firstName: String,
    val lastName: String
)

class CollectionUtilsTest {
    private val people = listOf(
        Person("Анна", "Смирнова"),
        Person("Пётр", "Иванов"),
        Person("John", "Smith")
    )

    private val retrievers = listOf<(Person) -> String>({ it.firstName }, { it.lastName })

    @Test
    fun `smartFilter matches ignoring case`() {
        // Act
        val result = people.smartFilter("JOHN", retrievers)

        // Assert
        assertEquals(listOf(people[2]), result)
    }

    @Test
    fun `smartFilter trims query`() {
        // Act
        val result = people.smartFilter("  Smith  ", retrievers)

        // Assert
        assertEquals(listOf(people[2]), result)
    }

    @Test
    fun `smartFilter treats yo and ye as equivalent`() {
        // Act
        val result = people.smartFilter("Петр", retrievers)

        // Assert
        assertEquals(listOf(people[1]), result)
    }

    @Test
    fun `smartFilter matches any of the string retrievers`() {
        // Act
        val result = people.smartFilter("смирнова", retrievers)

        // Assert
        assertEquals(listOf(people[0]), result)
    }

    @Test
    fun `smartFilter returns empty list when nothing matches`() {
        // Act
        val result = people.smartFilter("zzz", retrievers)

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `smartFilter returns all items for blank query`() {
        // Act
        val result = people.smartFilter("   ", retrievers)

        // Assert
        assertEquals(people, result)
    }

    @Test
    fun `prepareTextForSearch trims lowercases and replaces yo`() {
        // Act
        val result = " ПёТр ".prepareTextForSearch()

        // Assert
        assertEquals("петр", result)
    }
}
