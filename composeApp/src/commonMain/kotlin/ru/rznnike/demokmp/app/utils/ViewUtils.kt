package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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