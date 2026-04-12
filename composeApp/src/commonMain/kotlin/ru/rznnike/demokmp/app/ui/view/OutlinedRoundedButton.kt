package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.app.utils.ClicksFilter
import java.time.Clock

@Composable
fun OutlinedRoundedButton(
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
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val clicksFilter = remember { ClicksFilter(clock, clicksFilterMs) }

    OutlinedButton(
        onClick = {
            clicksFilter.filter {
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}