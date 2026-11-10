package ru.rznnike.demokmp.app.utils

import android.app.Application
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `Clipboard.setText` is a fire-and-forget bridge: it wraps the text in a plain-text [ClipEntry]
 * and hands it to Compose's [Clipboard] from the supplied scope.
 *
 * Robolectric is required because [ClipEntry] wraps a real `android.content.ClipData`; the scope is
 * the seam under test, so the write must not happen before the scope runs.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
class ClipboardUtilsAndroidTest {
    private class FakeClipboard : Clipboard {
        val entries = mutableListOf<ClipEntry?>()

        override suspend fun getClipEntry(): ClipEntry? = entries.lastOrNull()

        override suspend fun setClipEntry(clipEntry: ClipEntry?) {
            entries += clipEntry
        }
    }

    private fun FakeClipboard.getData() = entries.single()?.clipData?.getItemAt(0)?.text?.toString()

    @Test
    fun `setText dispatches the clipboard write on the supplied scope`() = runTest {
        // Arrange
        val clipboard = FakeClipboard()

        // Act
        clipboard.setText(text = "hello", scope = this)

        // Assert
        assertTrue(clipboard.entries.isEmpty(), "the write must not happen eagerly")

        advanceUntilIdle()

        assertEquals("hello", clipboard.getData())
    }

    @Test
    fun `setText preserves unicode and newlines`() = runTest {
        // Arrange
        val clipboard = FakeClipboard()
        val text = "line 1\nПривет 🎉\tline 2"

        // Act
        clipboard.setText(text = text, scope = this)
        advanceUntilIdle()

        // Assert
        assertEquals(text, clipboard.getData())
    }
}
