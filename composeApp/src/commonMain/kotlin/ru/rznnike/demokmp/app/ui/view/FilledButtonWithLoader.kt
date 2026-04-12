package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.PreviewAppTheme

@Composable
fun FilledButtonWithLoader(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    showLoader: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        FilledButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            content = content
        )
        if (showLoader) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = colors.containerColor,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colors.contentColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilledButtonWithLoaderPreview() = PreviewAppTheme {
    Column {
        FilledButtonWithLoader(
            modifier = Modifier.width(300.dp),
            onClick = {}
        ) {
            Text("Test")
        }
        Spacer(Modifier.height(16.dp))
        FilledButtonWithLoader(
            modifier = Modifier.width(300.dp),
            onClick = {},
            showLoader = true
        ) {
            Text("Test2")
        }
    }
}