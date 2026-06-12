package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.domain.model.common.UiScale

@Composable
expect fun Modifier.onClick(
    onDoubleClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier

@Composable
fun CustomUiScale(
    scale: UiScale,
    content: @Composable () -> Unit
) {
    val defaultDensity = LocalDensity.current
    val customDensity = Density(
        density = defaultDensity.density * scale.value / 100f,
        fontScale = defaultDensity.fontScale
    )
    CompositionLocalProvider(
        LocalDensity provides customDensity
    ) {
        content()
    }
}

@Composable
fun Modifier.cardBackground(): Modifier = background(
    color = MaterialTheme.colorScheme.surface,
    shape = MaterialTheme.shapes.medium
)

@Composable
fun Modifier.cardAlterBackground(): Modifier = background(
    color = MaterialTheme.colorScheme.background,
    shape = MaterialTheme.shapes.medium
)

@Composable
fun Modifier.dashedBorder(
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
): Modifier = drawWithContent {
    val outline = shape.createOutline(size, layoutDirection, density = this)
    val dashedStroke = Stroke(
        cap = cap,
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )
    drawContent()
    drawOutline(
        outline = outline,
        style = dashedStroke,
        color = color
    )
}