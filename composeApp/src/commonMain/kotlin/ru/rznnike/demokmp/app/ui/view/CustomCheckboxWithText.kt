package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.app.utils.onClick

@Composable
fun CustomCheckboxWithText(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    checkboxSize: Dp = 24.dp,
    contentPadding: Dp = 8.dp,
    textRes: StringResource,
    textStyle: TextStyle = LocalTextStyle.current,
    textColor: Color = Color.Unspecified,
    checked: Boolean,
    enabled: Boolean = true
) = Row(
    modifier = modifier.onClick {
        if (enabled) onClick()
    },
    verticalAlignment = Alignment.CenterVertically
) {
    CustomCheckbox(
        modifier = Modifier.size(checkboxSize),
        checked = checked,
        enabled = enabled
    )
    Spacer(Modifier.width(contentPadding))
    TextR(
        textRes = textRes,
        style = textStyle,
        color = if (enabled) textColor else LocalCustomColorScheme.current.disabledText
    )
}