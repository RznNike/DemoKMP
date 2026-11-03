package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

private const val CHAIN_TAG = "addIf-chain"

/**
 * `Modifier.addIf` takes a `@Composable` chain lambda, so it can only be invoked from a composition.
 * That makes this the first Compose UI test in the project — it lives in `desktopTest` because the
 * UI-test APIs are not available to `testDebugUnitTest` without Robolectric or a device.
 *
 * The applied chain is observed through `testTag`, the standard way to assert a [Modifier] was
 * applied without pixel snapshots.
 */
class ComposeUtilsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `addIf applies the chain when the condition is true`() {
        // Act
        rule.setContent {
            Box(Modifier.addIf(condition = true) { testTag(CHAIN_TAG) })
        }

        // Assert
        rule.onNodeWithTag(CHAIN_TAG).assertExists()
    }

    @Test
    fun `addIf returns the receiver unchanged when the condition is false`() {
        // Act
        rule.setContent {
            Box(Modifier.addIf(condition = false) { testTag(CHAIN_TAG) })
        }

        // Assert
        rule.onNodeWithTag(CHAIN_TAG).assertDoesNotExist()
    }
}
