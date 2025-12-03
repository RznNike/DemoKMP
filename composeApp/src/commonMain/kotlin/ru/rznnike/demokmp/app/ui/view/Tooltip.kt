package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.isBlank

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
    tooltip: String,
    modifier: Modifier = Modifier,
    alignment: TooltipAlignment = TooltipAlignment.CURSOR,
    content: @Composable (() -> Unit)
) = if (tooltip.isBlank()) {
    content()
} else {
    TooltipArea(
        modifier = modifier,
        delayMillis = 500,
        tooltipPlacement = when (alignment) {
            TooltipAlignment.CURSOR -> TooltipPlacement.CursorPoint(
                offset = DpOffset(8.dp, 20.dp)
            )

            TooltipAlignment.TOP -> TooltipPlacement.ComponentRect(
                anchor = Alignment.TopCenter,
                alignment = Alignment.TopCenter,
                offset = DpOffset(x = 0.dp, y = (-4).dp)
            )

            TooltipAlignment.BOTTOM -> TooltipPlacement.ComponentRect(
                anchor = Alignment.BottomCenter,
                alignment = Alignment.BottomCenter,
                offset = DpOffset(x = 0.dp, y = 4.dp)
            )
        },
        tooltip = {
            Surface(
                shadowElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = tooltip,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        content = content
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
    tooltipRes: StringResource,
    modifier: Modifier = Modifier,
    alignment: TooltipAlignment = TooltipAlignment.CURSOR,
    content: @Composable (() -> Unit)
) = Tooltip(
    tooltip = stringResource(tooltipRes),
    modifier = modifier,
    alignment = alignment,
    content = content
)

enum class TooltipAlignment {
    CURSOR,
    TOP,
    BOTTOM
}