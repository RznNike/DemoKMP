package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.ui.theme.bodyMediumMono
import ru.rznnike.demokmp.app.utils.backgroundColor
import ru.rznnike.demokmp.app.utils.highlightSubstrings
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.domain.log.*
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.currentTimeMillis
import ru.rznnike.demokmp.domain.utils.toDateString
import java.util.*

@Composable
fun LogNetworkMessageItem(
    message: LogNetworkMessage,
    query: String,
    onClick: () -> Unit
) {
    DisableSelection {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(message.state.backgroundColor)
                .onClick(onClick)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            @Composable
            fun MessageRow(
                message: LogMessage,
                removeHttpAddress: Boolean = false
            ) = Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
                    style = MaterialTheme.typography.bodyMediumMono
                )
                Spacer(Modifier.width(8.dp))
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.width(8.dp))

                var formattedMessage = message.getFormattedMessage()
                    .lines()
                    .first()
                if (removeHttpAddress) {
                    formattedMessage = formattedMessage.replace(Regex("http(s?)://.+ \\("), "(")
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = formattedMessage.highlightSubstrings(query),
                    style = MaterialTheme.typography.bodyMediumMono
                )
            }

            MessageRow(message.request)
            message.response?.let {
                MessageRow(
                    message = it,
                    removeHttpAddress = true
                )
            }
        }
    }
}


@Preview
@Composable
private fun LogNetworkMessageItemPreview() {
    AppTheme {
        LogNetworkMessageItem(
            message = LogNetworkMessage(
                uuid = UUID.randomUUID(),
                request = LogMessage(
                    type = LogType.NETWORK,
                    level = LogLevel.DEBUG,
                    timestamp = currentTimeMillis(),
                    tag = "",
                    message = "Test request"
                ),
                response = LogMessage(
                    type = LogType.NETWORK,
                    level = LogLevel.DEBUG,
                    timestamp = currentTimeMillis(),
                    tag = "",
                    message = "Test response"
                ),
                state = NetworkRequestState.SUCCESS
            ),
            query = "",
            onClick = {}
        )
    }
}