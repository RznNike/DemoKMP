package ru.rznnike.demokmp.app.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun Tooltip(
    tooltip: String,
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    alignment: TooltipAlignment = TooltipAlignment.CURSOR,
    content: @Composable (() -> Unit)
)

@Composable
fun Tooltip(
    tooltipRes: StringResource,
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    alignment: TooltipAlignment = TooltipAlignment.CURSOR,
    content: @Composable (() -> Unit)
) = Tooltip(
    tooltip = stringResource(tooltipRes),
    modifier = modifier,
    popupModifier = popupModifier,
    alignment = alignment,
    content = content
)

enum class TooltipAlignment {
    CURSOR,
    TOP,
    TOP_START,
    BOTTOM
}