package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.bodyLargeBold

@Composable
fun TabText(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false
) = Box(
    modifier = modifier.width(IntrinsicSize.Max)
) {
    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = text,
        style = MaterialTheme.typography.bodyLargeBold
    )
    if (selected) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .align(Alignment.BottomCenter),
        )
    }
}