package ru.rznnike.demokmp.data.utils.json

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UUIDSerializerTest {
    private val uuidString = "3f2504e0-4f89-11d3-9a0c-0305e82c3301"
    private val uuid = UUID.fromString(uuidString)

    @Test
    fun `serialize produces the canonical UUID string`() {
        // Act
        val result = defaultJson.encodeToString(UUIDSerializer(), uuid)

        // Assert
        assertEquals("\"$uuidString\"", result)
    }

    @Test
    fun `deserialize accepts a canonical string`() {
        // Act
        val result = defaultJson.decodeFromString(UUIDSerializer(), "\"$uuidString\"")

        // Assert
        assertEquals(uuid, result)
    }

    @Test
    fun `round-trips through defaultJson`() {
        // Act
        val json = defaultJson.encodeToString(UUIDSerializer(), uuid)
        val result = defaultJson.decodeFromString(UUIDSerializer(), json)

        // Assert
        assertEquals(uuid, result)
    }

    @Test
    fun `invalid UUID string throws instead of falling back`() {
        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            defaultJson.decodeFromString(UUIDSerializer(), "\"not-a-uuid\"")
        }
    }
}
