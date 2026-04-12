package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.extraSmallCorners
import ru.rznnike.demokmp.app.utils.ClicksFilter
import java.time.Clock

@Composable
fun SelectableButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
    clicksFilterMs: Long = 0,
    clock: Clock = Clock.systemUTC(),
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showLoader: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderWidth = 2.dp
    val clicksFilter = remember { ClicksFilter(clock, clicksFilterMs) }

    FilledButtonWithLoader(
        onClick = {
            clicksFilter.filter {
                onClick()
            }
        },
        modifier = modifier
            .let {
                if (isFocused) {
                    it.border(
                        width = borderWidth,
                        color = colors.containerColor,
                        shape = RoundedCornerShape(extraSmallCorners + borderWidth)
                    )
                } else {
                    it
                }
            }
            .padding(borderWidth * 2),
        buttonModifier = buttonModifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = null,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        showLoader = showLoader,
        content = content
    )
}