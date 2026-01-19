package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun Tooltip(
    tooltip: String,
    modifier: Modifier = Modifier,
    alignment: TooltipAlignment = TooltipAlignment.CURSOR,
    content: @Composable (() -> Unit)
)

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