package ru.rznnike.demokmp.app.ui.viewmodel.global.hotkeys

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import ru.rznnike.demokmp.app.model.common.HotkeyDescription

/**
 * `HotKeysViewModel` lives in `desktopMain` (`KeyEvent` is a desktop actual), so the test belongs in
 * `desktopTest`. The ViewModel has no collaborators and no coroutines, so no Koin module and no
 * `ViewModelTestRule` are needed.
 */
class HotKeysViewModelTest {
    @Test
    fun `new view model exposes empty hotkey descriptions`() {
        // Act
        val viewModel = HotKeysViewModel()

        // Assert
        assertEquals(emptyList(), viewModel.uiState.value.screenHotkeysDescription)
        assertEquals(emptyList(), viewModel.uiState.value.commonHotkeysDescription)
    }

    @Test
    fun `setScreenHotkeysDescription replaces the screen hotkeys`() {
        // Arrange
        val viewModel = HotKeysViewModel()
        val hotkeys = listOf(HotkeyDescription(hotkey = "Ctrl+S", description = "Save"))

        // Act
        viewModel.setScreenHotkeysDescription(hotkeys)

        // Assert
        assertEquals(hotkeys, viewModel.uiState.value.screenHotkeysDescription)
    }

    @Test
    fun `setCommonHotkeysDescription replaces the common hotkeys`() {
        // Arrange
        val viewModel = HotKeysViewModel()
        val hotkeys = listOf(
            HotkeyDescription(hotkey = "F1", description = "Help"),
            HotkeyDescription(hotkey = "Esc", description = "Close")
        )

        // Act
        viewModel.setCommonHotkeysDescription(hotkeys)

        // Assert
        assertEquals(hotkeys, viewModel.uiState.value.commonHotkeysDescription)
    }

    @Test
    fun `setScreenEventListener replaces the stored listener`() {
        // Arrange
        val viewModel = HotKeysViewModel()
        val listener = HotKeysViewModel.EventListener { }

        // Act
        viewModel.setScreenEventListener(listener)

        // Assert
        assertSame(listener, viewModel.uiState.value.screenEventListener)
    }
}
