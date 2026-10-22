package ru.rznnike.demokmp.data.storage.entity

import ru.rznnike.demokmp.domain.model.db.DBExampleData
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("SameParameterValue")
class DBExampleDataEntityTest {
    private fun entity(
        id: Long = 42,
        name: String = "name"
    ) = DBExampleDataEntity(
        id = id,
        name = name
    )

    private fun domainData(
        id: Long = 42,
        name: String = "name"
    ) = DBExampleData(
        id = id,
        name = name
    )

    @Test
    fun `toDBExampleData maps id and name`() {
        // Arrange
        val entity = entity(id = 7, name = "test name")

        // Act
        val result = entity.toDBExampleData()

        // Assert
        assertEquals(domainData(id = 7, name = "test name"), result)
    }

    @Test
    fun `toDBExampleDataEntity maps id and name`() {
        // Arrange
        val data = domainData(id = 7, name = "test name")

        // Act
        val result = data.toDBExampleDataEntity()

        // Assert
        assertEquals(entity(id = 7, name = "test name"), result)
    }

    @Test
    fun `round trip through the entity preserves the data`() {
        // Arrange
        val original = entity(id = 7, name = "test name")

        // Act
        val result = original.toDBExampleData().toDBExampleDataEntity()

        // Assert
        assertEquals(original, result)
    }

    @Test
    fun `round trip through the entity preserves the default id`() {
        // Arrange
        val original = entity(id = 0, name = "test name")

        // Act
        val result = original.toDBExampleData().toDBExampleDataEntity()

        // Assert
        assertEquals(0, result.id)
    }
}
