package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.rznnike.demokmp.app.ui.theme.PreviewDesktopAppTheme
import ru.rznnike.demokmp.app.ui.theme.bodyLargeBold
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.force_close
import ru.rznnike.demokmp.generated.resources.please_wait
import ru.rznnike.demokmp.generated.resources.shutdown

private const val FORCE_CLOSE_BUTTON_DELAY_MS = 5000L

@Composable
fun AppClosingPlaceholder(
    forceCloseCallback: () -> Unit
) {
    val appClosingFocusRequester = remember { FocusRequester() }
    var isForceCloseButtonVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface
            )
            .focusRequester(appClosingFocusRequester)
            .onFocusChanged { state ->
                if (state.isFocused) {
                    appClosingFocusRequester.captureFocus()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        CircularProgressIndicator(
            modifier = Modifier
                .size(60.dp)
                .focusable(),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(32.dp))
        TextR(
            textRes = Res.string.shutdown,
            style = MaterialTheme.typography.bodyLargeBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        TextR(
            textRes = Res.string.please_wait,
            style = MaterialTheme.typography.bodyLargeBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(32.dp))
        OutlinedRoundedButton(
            modifier = Modifier.alpha(if (isForceCloseButtonVisible) 1f else 0f),
            onClick = {
                if (isForceCloseButtonVisible) {
                    forceCloseCallback()
                }
            }
        ) {
            TextR(Res.string.force_close)
        }
        Spacer(Modifier.weight(1f))
    }

    LaunchedEffect(Unit) {
        appClosingFocusRequester.requestFocus()
        delay(FORCE_CLOSE_BUTTON_DELAY_MS)
        isForceCloseButtonVisible = true
    }
}

@Preview
@Composable
private fun AppClosingPlaceholderPreview() = PreviewDesktopAppTheme {
    AppClosingPlaceholder(
        forceCloseCallback = { }
    )
}