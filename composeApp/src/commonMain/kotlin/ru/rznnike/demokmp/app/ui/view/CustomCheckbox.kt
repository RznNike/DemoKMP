package ru.rznnike.demokmp.app.ui.view

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_checkbox_off
import ru.rznnike.demokmp.generated.resources.ic_checkbox_on

@Composable
fun CustomCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean = true
) = Icon(
    modifier = modifier,
    painter = painterResource(
        if (checked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off
    ),
    tint = when {
        !enabled -> LocalCustomColorScheme.current.disabledText
        checked -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    },
    contentDescription = null
)