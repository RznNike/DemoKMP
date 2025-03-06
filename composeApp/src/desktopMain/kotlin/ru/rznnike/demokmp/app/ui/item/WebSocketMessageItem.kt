package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage

@Composable
fun WebSocketMessageItem(
    message: WebSocketMessage
) {
    Row {
        if (message.isIncoming) {
            Surface(
                modifier = Modifier.weight(7f),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    text = message.text
                )
            }
            Spacer(modifier = Modifier.weight(3f))
        } else {
            Spacer(modifier = Modifier.weight(3f))
            Surface(
                modifier = Modifier.weight(7f),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    text = message.text,
                    textAlign = TextAlign.End
                )
            }
        }
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