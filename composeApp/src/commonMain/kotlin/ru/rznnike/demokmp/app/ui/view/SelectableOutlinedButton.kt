package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import java.time.Clock

@Composable
fun SelectableOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clicksFilterMs: Long = 0,
    clock: Clock = Clock.systemUTC(),
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = LocalCustomColorScheme.current.outlineComponentContent,
        disabledContentColor = LocalCustomColorScheme.current.disabledText
    ),
    elevation: ButtonElevation? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    OutlinedRoundedButton(
        modifier = modifier,
        onClick = onClick,
        clicksFilterMs = clicksFilterMs,
        clock = clock,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            color = when {
                isFocused -> MaterialTheme.colorScheme.primary
                enabled -> MaterialTheme.colorScheme.outline
                else -> LocalCustomColorScheme.current.disabledText
            }
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}