package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.app.ui.theme.PreviewAppTheme
import ru.rznnike.demokmp.app.ui.theme.bodyMediumMono
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.utils.currentTimeMillis
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.current_session_start
import ru.rznnike.demokmp.generated.resources.session_start

@Composable
fun LogMessageServiceItem(
    type: LogType,
    timestamp: Long,
    isCurrentSession: Boolean
) {
    var text = ""
    var background = Color.Unspecified
    when (type) {
        LogType.SESSION_START -> {
            val headerRes: StringResource
            if (isCurrentSession) {
                headerRes = Res.string.current_session_start
                background = LocalCustomColorScheme.current.logServiceAccent
            } else {
                headerRes = Res.string.session_start
                background = LocalCustomColorScheme.current.logService
            }
            text = "%s | %s".format(
                stringResource(headerRes),
                timestamp.toDateString()
            )
        }
        else -> Unit
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(background)
            .padding(4.dp),
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMediumMono,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Preview
@Composable
private fun LogMessageServiceItemPreview() {
    PreviewAppTheme {
        Column {
            LogMessageServiceItem(
                type = LogType.SESSION_START,
                timestamp = currentTimeMillis(),
                isCurrentSession = true
            )
            LogMessageServiceItem(
                type = LogType.SESSION_START,
                timestamp = currentTimeMillis(),
                isCurrentSession = false
            )
        }
    }
}