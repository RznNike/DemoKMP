package ru.rznnike.demokmp.app.utils

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.ViewModel
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Covers the `LocalWindowViewModelStoreOwner` guard and the store scoping done by
 * `WithWindowViewModelStoreOwner`. Both are desktop-only (`windowViewModel` is an `actual` in
 * `desktopMain`), so this lives in `desktopTest`.
 */
class WindowViewModelDesktopTest {
    @get:Rule
    val rule = createComposeRule()

    class TestViewModel : ViewModel()

    @Test
    fun `windowViewModel without a provided owner fails with a descriptive message`() {
        // Act
        val error = assertFailsWith<IllegalStateException> {
            rule.setContent {
                windowViewModel<TestViewModel>()
            }
            rule.waitForIdle()
        }

        // Assert
        val message = error.message.orEmpty()
        assertTrue(
            message.contains("No ViewModelStoreOwner was provided via LocalWindowViewModelStoreOwner"),
            "Unexpected message: $message"
        )
    }

    @Test
    fun `WithWindowViewModelStoreOwner provides an owner that resolves a ViewModel`() {
        // Arrange
        var viewModel: TestViewModel? = null

        // Act
        rule.setContent {
            WithWindowViewModelStoreOwner {
                viewModel = windowViewModel<TestViewModel> { TestViewModel() }
            }
        }
        rule.waitForIdle()

        // Assert
        assertNotNull(viewModel)
    }

    @Test
    fun `the same key returns the same instance while a different key returns a new one`() {
        // Arrange
        var first: TestViewModel? = null
        var second: TestViewModel? = null
        var other: TestViewModel? = null

        // Act
        rule.setContent {
            WithWindowViewModelStoreOwner {
                first = windowViewModel<TestViewModel>(key = "shared") { TestViewModel() }
                second = windowViewModel<TestViewModel>(key = "shared") { TestViewModel() }
                other = windowViewModel<TestViewModel>(key = "other") { TestViewModel() }
            }
        }
        rule.waitForIdle()

        // Assert
        assertSame(first, second)
        assertNotSame(first, other)
    }
}
