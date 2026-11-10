package ru.rznnike.demokmp.app.utils

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private const val COUNTER_TEXT_TAG = "compose-host-counter"
private const val INCREMENT_BUTTON_TAG = "compose-host-increment"

/**
 * Proves the Android Compose UI-test host works in `androidUnitTest` (Robolectric, no device):
 * composition, semantics lookup, input dispatch and recomposition.
 *
 * It deliberately does not repeat `ComposeUtilsTest` (`Modifier.addIf`) — that test lives in
 * `desktopTest` and only needs a composition, not an Activity-hosted content view.
 *
 * `createComposeRule()` (v2) hosts content in an `androidx.activity.ComponentActivity`, which is
 * supplied by `androidx.compose.ui:ui-test-manifest` via `debugImplementation`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ComposeUiHostSmokeTest { // TODO remove when real UI tests added
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `compose host renders content and dispatches a click`() {
        // Arrange
        rule.setContent {
            var clicks by remember { mutableIntStateOf(0) }

            Column {
                Text(
                    text = "clicks: $clicks",
                    modifier = Modifier.testTag(COUNTER_TEXT_TAG)
                )
                Button(
                    onClick = { clicks++ },
                    modifier = Modifier.testTag(INCREMENT_BUTTON_TAG)
                ) {
                    Text("Increment")
                }
            }
        }

        // Assert initial render
        rule.onNodeWithTag(COUNTER_TEXT_TAG).assertTextEquals("clicks: 0")

        // Act
        rule.onNodeWithTag(INCREMENT_BUTTON_TAG).performClick()
        rule.waitForIdle()

        // Assert recomposition after input dispatch
        rule.onNodeWithTag(COUNTER_TEXT_TAG).assertTextEquals("clicks: 1")
    }
}
