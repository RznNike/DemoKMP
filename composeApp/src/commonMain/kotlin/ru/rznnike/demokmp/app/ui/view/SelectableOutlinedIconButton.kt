package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.app.utils.ClicksFilter
import java.time.Clock

@Composable
fun SelectableOutlinedIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clicksFilterMs: Long = 0,
    clock: Clock = Clock.systemUTC(),
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors().copy(
        contentColor = MaterialTheme.colorScheme.primary
    ),
    iconColor: Color = LocalCustomColorScheme.current.outlineComponentContent,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    iconRes: DrawableResource
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val clicksFilter = remember { ClicksFilter(clock, clicksFilterMs) }

    OutlinedIconButton(
        onClick = {
            clicksFilter.filter {
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        border = BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            color = when {
                isFocused -> MaterialTheme.colorScheme.primary
                enabled -> MaterialTheme.colorScheme.outline
                else -> LocalCustomColorScheme.current.disabledText
            }
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            tint = if (enabled) iconColor else LocalCustomColorScheme.current.disabledText,
            contentDescription = null
        )
    }
}