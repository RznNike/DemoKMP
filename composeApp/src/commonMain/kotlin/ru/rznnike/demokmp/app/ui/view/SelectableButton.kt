package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.extraSmallCorners
import kotlin.plus

@Composable
fun SelectableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderWidth = 2.dp

    Button(
        onClick = onClick,
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
            .padding(4.dp),
        enabled = enabled,
        shape = RoundedCornerShape(extraSmallCorners),
        colors = colors,
        elevation = elevation,
        border = null,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}