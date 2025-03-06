package ru.rznnike.demokmp.app.ui.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.ic_delete

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    leftButton: ToolbarButton? = null,
    rightButton: ToolbarButton? = null
) = Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = MaterialTheme.shapes.medium
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        @Composable
        fun ImageButton(
            button: ToolbarButton?
        ) = Box(
            modifier = Modifier.size(40.dp)
        ) {
            button?.let {
                SelectableOutlinedIconButton(
                    modifier = Modifier.fillMaxSize(),
                    iconRes = button.iconRes,
                    onClick = {
                        button.onClick()
                    }
                )
            }
        }

        ImageButton(leftButton)
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.width(16.dp))
        ImageButton(rightButton)
    }
}

data class ToolbarButton(
    val iconRes: DrawableResource,
    val iconTint: Color? = null,
    val onClick: () -> Unit
)

@Preview
@Composable
private fun ToolbarPreview() {
    Toolbar(
        title = "Test title",
        leftButton = ToolbarButton(Res.drawable.ic_back) { },
        rightButton = ToolbarButton(Res.drawable.ic_delete) { }
    )
}