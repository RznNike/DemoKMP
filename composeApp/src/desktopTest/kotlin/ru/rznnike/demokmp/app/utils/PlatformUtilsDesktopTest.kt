package ru.rznnike.demokmp.app.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val MAC_ADDRESS_PATTERN = Regex("^([0-9A-F]{2}-){5}[0-9A-F]{2}$")

/**
 * Lives in `desktopTest` on purpose: both targets under test are JVM/desktop-only
 * (`NetworkInterface`, `System.getProperty`).
 *
 * The network-interface selection in `getMacAddress()` cannot be driven deterministically without
 * faking `NetworkInterface` (a final JDK class), so only the formatting half is asserted exactly and
 * the selection half is checked for its contract: empty string or a well-formed MAC address.
 */
class PlatformUtilsDesktopTest {
    @Test
    fun `formatMacAddress returns an empty string for an empty address`() {
        // Act
        val result = byteArrayOf().formatMacAddress()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `formatMacAddress joins the bytes with dashes as uppercase hex`() {
        // Act
        val result = byteArrayOf(0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E).formatMacAddress()

        // Assert
        assertEquals("00-1A-2B-3C-4D-5E", result)
    }

    @Test
    fun `formatMacAddress formats high bit bytes as unsigned hex`() {
        // Act
        val result = byteArrayOf(0x80.toByte(), 0xFF.toByte(), 0x0A, 0x7F).formatMacAddress()

        // Assert
        assertEquals("80-FF-0A-7F", result)
    }

    @Test
    fun `getMacAddress returns null or a well formed MAC address`() {
        // Act
        val result = getMacAddress()

        // Assert
        assertTrue(
            result == null || MAC_ADDRESS_PATTERN.matches(result),
            "Expected null or a MAC address in the AA-BB-CC-DD-EE-FF form, but was: $result"
        )
    }

    @Test
    fun `platformName describes the running JVM`() {
        // Act
        val result = platformName

        // Assert
        assertTrue(result.startsWith("Java "), "Unexpected platform name: $result")
        assertEquals(2, result.count { it == '|' }, "Unexpected platform name: $result")
    }
}
