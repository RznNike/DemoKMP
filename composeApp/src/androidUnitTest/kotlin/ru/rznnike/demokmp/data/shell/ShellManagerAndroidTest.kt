package ru.rznnike.demokmp.data.shell

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Android keeps only a stub shell: there is no PC to talk to. The value under test is the contract
 * the rest of the app relies on — every call completes and the PC data payload is empty rather than
 * throwing [NotImplementedError].
 */
class ShellManagerAndroidTest {
    private val shellManager = getShellManager()

    @Test
    fun `getPCData is an empty string on android`() = runTest {
        assertEquals("", shellManager.getPCData())
    }

    @Test
    fun `shell lifecycle and window operations complete on android`() = runTest {
        // Arrange
        val result = runCatching {
            shellManager.initWrapper()
            shellManager.openFolderOrFile("C:/does/not/exist")
            shellManager.minimizeAllWindows()
            shellManager.destroyWrapper()
        }

        // Assert
        assertTrue(result.isSuccess, "the android shell stub must not throw")
    }
}
