package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.ui.theme.bodyMediumMono
import ru.rznnike.demokmp.app.ui.theme.bodySmallMono
import ru.rznnike.demokmp.app.ui.view.LinkifyText
import ru.rznnike.demokmp.app.utils.backgroundColor
import ru.rznnike.demokmp.app.utils.highlightSubstrings
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.currentTimeMillis
import ru.rznnike.demokmp.domain.utils.toDateString

@Composable
fun LogMessageItem(
    message: LogMessage,
    query: String,
    filterOnlyByTag: Boolean,
    collapseNetworkMessages: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(message.level.backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .width(110.dp)
                .align(Alignment.Top)
        ) {
            Text(
                text = message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
                style = MaterialTheme.typography.bodyMediumMono
            )
            if (message.tag.isNotBlank()) {
                SelectionContainer(
                    modifier = Modifier.focusProperties {
                        canFocus = false
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = message.tag.highlightSubstrings(query),
                        style = MaterialTheme.typography.bodySmallMono,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.width(8.dp))
        var formattedMessage = message.getFormattedMessage()
        if (collapseNetworkMessages && (message.type == LogType.NETWORK)) {
            formattedMessage = formattedMessage.lines().first()
        }

        LinkifyText(
            text = formattedMessage.highlightSubstrings(
                if (filterOnlyByTag) "" else query
            ),
            style = MaterialTheme.typography.bodyMediumMono
        )
    }
}

@Preview
@Composable
private fun LogMessageItemPreview() {
    AppTheme {
        Column {
            LogMessageItem(
                message = LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.DEBUG,
                    timestamp = currentTimeMillis(),
                    tag = "PaymentTerminal",
                    message = "Test message"
                ),
                query = "",
                filterOnlyByTag = false,
                collapseNetworkMessages = false
            )
            LogMessageItem(
                message = LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.DEBUG,
                    timestamp = currentTimeMillis(),
                    tag = "",
                    message = "Test message"
                ),
                query = "",
                filterOnlyByTag = false,
                collapseNetworkMessages = false
            )
            LogMessageItem(
                message = LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.DEBUG,
                    timestamp = currentTimeMillis(),
                    tag = "TestTag",
                    message = "Test message\nSecond line\nThird line"
                ),
                query = "",
                filterOnlyByTag = false,
                collapseNetworkMessages = false
            )
            LogMessageItem(
                message = LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.INFO,
                    timestamp = currentTimeMillis(),
                    tag = "TestTag",
                    message = "Test message"
                ),
                query = "",
                filterOnlyByTag = false,
                collapseNetworkMessages = false
            )
            LogMessageItem(
                message = LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.ERROR,
                    timestamp = currentTimeMillis(),
                    tag = "TestTag",
                    message = "Test message"
                ),
                query = "",
                filterOnlyByTag = false,
                collapseNetworkMessages = false
            )
        }
    }
}