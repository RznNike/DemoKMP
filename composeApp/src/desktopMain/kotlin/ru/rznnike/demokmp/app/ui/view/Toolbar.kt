package ru.rznnike.demokmp.app.ui.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.ic_back
import demokmp.composeapp.generated.resources.ic_delete
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    leftButton: ToolbarButton? = null,
    rightButton: ToolbarButton? = null
) {
    Row(modifier) {
        @Composable
        fun ImageButton(
            button: ToolbarButton?
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .onClick {
                        button?.onClick?.invoke()
                    },
            ) {
                button?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(button.iconRes),
                        colorFilter = ColorFilter.tint(
                            button.iconTint ?: MaterialTheme.colorScheme.onBackground
                        ),
                        contentDescription = null
                    )
                }
            }
        }

        ImageButton(leftButton)
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
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