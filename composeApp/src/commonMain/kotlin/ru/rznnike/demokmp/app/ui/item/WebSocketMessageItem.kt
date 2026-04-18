package ru.rznnike.demokmp.app.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage

@Composable
fun WebSocketMessageItem(
    message: WebSocketMessage
) = Row {
    if (!message.isIncoming) {
        Spacer(modifier = Modifier.weight(3f))
    }
    Text(
        modifier = Modifier
            .weight(7f)
            .background(
                color = if (message.isIncoming) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
            .fillMaxWidth(),
        text = message.text,
        textAlign = if (message.isIncoming) TextAlign.Start else TextAlign.End
    )
    if (message.isIncoming) {
        Spacer(modifier = Modifier.weight(3f))
    }
}
