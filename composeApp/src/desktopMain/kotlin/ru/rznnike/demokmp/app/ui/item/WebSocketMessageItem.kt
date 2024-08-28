package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

@Composable
fun WebSocketMessageItem(
    message: WebSocketMessage
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = if (message.isIncoming) TextAlign.Start else TextAlign.End,
        )
    }
}

@Preview
@Composable
private fun WebSocketMessageItemPreview() {
    WebSocketMessageItem(
        WebSocketMessage(
            text = "Test",
            isIncoming = true
        )
    )
}