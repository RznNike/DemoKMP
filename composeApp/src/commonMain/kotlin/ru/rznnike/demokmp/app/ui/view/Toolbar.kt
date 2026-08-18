package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import ru.rznnike.demokmp.app.ui.theme.PreviewAppTheme
import ru.rznnike.demokmp.app.utils.cardBackground
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.ic_menu

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    leftButton: ToolbarButton? = null,
    rightButton: ToolbarButton? = null
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .cardBackground(),
    verticalAlignment = Alignment.CenterVertically
) {
    @Composable
    fun ImageButton(
        button: ToolbarButton?
    ) = Box(
        modifier = Modifier
            .padding(16.dp)
            .size(40.dp)
    ) {
        button?.let {
            SelectableOutlinedIconButton(
                modifier = Modifier.fillMaxSize(),
                iconRes = button.iconRes,
                onClick = button.onClick
            )
            if (button.showIndicator) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd)
                )
            }
        }
    }

    ImageButton(leftButton)
    Text(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 16.sp,
            maxFontSize = 22.sp
        ),
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
    ImageButton(rightButton)
}

data class ToolbarButton(
    val iconRes: DrawableResource,
    val iconTint: Color = Color.Unspecified,
    val tooltip: String = "",
    val showIndicator: Boolean = false,
    val onClick: () -> Unit
)

@Preview
@Composable
private fun ToolbarPreview() = PreviewAppTheme {
    Toolbar(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        title = "My cool toolbar",
        leftButton = ToolbarButton(Res.drawable.ic_back) { },
        rightButton = ToolbarButton(
            iconRes = Res.drawable.ic_menu,
            showIndicator = true
        ) { }
    )
}