package ru.rznnike.demokmp.app.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `Clipboard.setText` is a fire-and-forget bridge to the AWT clipboard: it wraps the text in a
 * [StringSelection] and hands it to Compose's [Clipboard] from the supplied scope. The scope is the
 * seam under test, so the write must not happen before the scope runs.
 */
@OptIn(ExperimentalComposeUiApi::class)
class ClipboardUtilsDesktopTest {
    private class FakeClipboard : Clipboard {
        val entries = mutableListOf<ClipEntry?>()

        override suspend fun getClipEntry(): ClipEntry? = entries.lastOrNull()

        override suspend fun setClipEntry(clipEntry: ClipEntry?) {
            entries += clipEntry
        }
    }

    @Test
    fun `setText dispatches the clipboard write on the supplied scope`() = runTest {
        // Arrange
        val clipboard = FakeClipboard()

        // Act
        clipboard.setText(text = "hello", scope = this)

        // Assert
        assertTrue(clipboard.entries.isEmpty(), "the write must not happen eagerly")

        advanceUntilIdle()

        val selection = clipboard.entries.single()?.nativeClipEntry as StringSelection
        assertEquals("hello", selection.getTransferData(DataFlavor.stringFlavor))
    }

    @Test
    fun `setText preserves the exact text including unicode and newlines`() = runTest {
        // Arrange
        val clipboard = FakeClipboard()
        val text = "line 1\nПривет 🎉\tline 2"

        // Act
        clipboard.setText(text = text, scope = this)
        advanceUntilIdle()

        // Assert
        val selection = clipboard.entries.single()?.nativeClipEntry as StringSelection
        assertEquals(text, selection.getTransferData(DataFlavor.stringFlavor))
    }
}
