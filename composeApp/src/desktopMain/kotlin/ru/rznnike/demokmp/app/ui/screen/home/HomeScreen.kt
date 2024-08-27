package ru.rznnike.demokmp.app.ui.screen.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleFlow
import ru.rznnike.demokmp.app.ui.screen.httpexample.HTTPExampleFlow
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsFlow
import ru.rznnike.demokmp.app.ui.screen.wsexample.WebSocketsExampleFlow
import ru.rznnike.demokmp.app.ui.theme.bodyLargeItalic
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.utils.platformName

class HomeScreen : NavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Preview
    @Composable
    override fun Content() {
        val notifier: Notifier = koinInject()

        val flowNavigator = getFlowNavigator()

        var showAboutDialog by remember { mutableStateOf(false) }

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.main_screen)
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val verticalScrollState = rememberScrollState()
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(verticalScrollState)
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    @Composable
                    fun MenuButton(text: StringResource, onClick: () -> Unit) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            onClick = onClick
                        ) {
                            TextR(
                                modifier = Modifier.fillMaxWidth(),
                                textRes = text,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    MenuButton(Res.string.open_settings) {
                        flowNavigator.open(SettingsFlow())
                    }
                    MenuButton(Res.string.open_http_example) {
                        flowNavigator.open(HTTPExampleFlow())
                    }
                    MenuButton(Res.string.open_ws_example) {
                        flowNavigator.open(WebSocketsExampleFlow())
                    }
                    MenuButton(Res.string.open_db_example) {
                        flowNavigator.open(DBExampleFlow())
                    }
                    MenuButton(Res.string.about_app) {
                        showAboutDialog = true
                    }
                    MenuButton(Res.string.test_dialog) {
                        notifier.sendAlert(Res.string.test_dialog)
                    }
                    MenuButton(Res.string.test_message) {
                        notifier.sendActionMessage(Res.string.test_message, Res.string.close) {}
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(verticalScrollState)
                )
            }
            TextR(
                textRes = Res.string.hotkeys_tip,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLargeItalic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }

        if (showAboutDialog) {
            val details = "%s: %s.%d%s\n%s: %s".format(
                stringResource(Res.string.version),
                BuildKonfig.VERSION_NAME,
                BuildKonfig.VERSION_CODE,
                if (BuildKonfig.DEBUG) " debug" else "",
                stringResource(Res.string.environment),
                platformName
            )
            CommonAlertDialog(
                type = AlertDialogType.HORIZONTAL,
                header = stringResource(Res.string.window_title),
                message = details,
                cancellable = true,
                onCancelListener = {
                    showAboutDialog = false
                },
                actions = listOf(
                    AlertDialogAction(stringResource(Res.string.close)) {
                        showAboutDialog = false
                    }
                )
            )
        }
    }
}