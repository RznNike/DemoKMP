package ru.rznnike.demokmp.app.ui.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.ui.theme.PreviewAppTheme
import ru.rznnike.demokmp.app.utils.getFormattedAppVersion
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_keyboard
import ru.rznnike.demokmp.generated.resources.ic_logger
import ru.rznnike.demokmp.generated.resources.ic_zoom
import ru.rznnike.demokmp.generated.resources.scale
import ru.rznnike.demokmp.generated.resources.tootip_hotkeys
import ru.rznnike.demokmp.generated.resources.tootip_logger

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomStatusBar(
    isVisible: Boolean = true,
    uiScale: UiScale,
    onLoggerClick: () -> Unit,
    onKeyboardShortcutsClick: () -> Unit,
    onUiScaleChanged: (UiScale) -> Unit
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .height(32.dp)
        .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    if (isVisible) {
        Spacer(Modifier.weight(1f))
        Tooltip(
            tooltipRes = Res.string.tootip_logger,
            alignment = TooltipAlignment.TOP
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .onClick { onLoggerClick() },
                painter = painterResource(Res.drawable.ic_logger),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(12.dp))
        Tooltip(
            tooltipRes = Res.string.tootip_hotkeys,
            alignment = TooltipAlignment.TOP
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .onClick { onKeyboardShortcutsClick() },
                painter = painterResource(Res.drawable.ic_keyboard),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(12.dp))

        val showScalePopup = remember { mutableStateOf(false) }
        Box {
            Tooltip(
                tooltipRes = Res.string.scale,
                alignment = TooltipAlignment.TOP
            ) {
                Row(
                    modifier = Modifier.onClick {
                        showScalePopup.value = true
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(Res.drawable.ic_zoom),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "%d%%".format(uiScale.value),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            PopupList(
                modifier = Modifier.width(75.dp),
                showPopup = showScalePopup,
                alignment = Alignment.BottomCenter,
                verticalOffset = (-20).dp,
                items = UiScale.entries.sortedDescending(),
                itemNameRetriever = { "%d%%".format(it?.value) },
                onItemSelected = onUiScaleChanged
            )
        }
        Spacer(Modifier.width(16.dp))

        val version = remember { getFormattedAppVersion() }
        Text(
            text = version,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun BottomStatusBarPreview() {
    PreviewAppTheme {
        BottomStatusBar(
            uiScale = UiScale.S100,
            onLoggerClick = { },
            onKeyboardShortcutsClick = { },
            onUiScaleChanged = { }
        )
    }
}