package ru.rznnike.demokmp.app.ui.screen.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.*
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleFlow
import ru.rznnike.demokmp.app.ui.screen.networkexample.NetworkExampleFlow
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsFlow
import ru.rznnike.demokmp.app.utils.TextR

class HomeScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val notifier: Notifier = koinInject()

        val flowNavigator = getFlowNavigator()

        Column {
            Spacer(Modifier.height(20.dp))
            TextR(
                textRes = Res.string.main_screen_title,
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val verticalScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .verticalScroll(verticalScrollState)
                        .fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            flowNavigator.open(SettingsFlow())
                        }
                    ) {
                        TextR(Res.string.open_settings)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            flowNavigator.open(NetworkExampleFlow())
                        }
                    ) {
                        TextR(Res.string.open_network_example)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            flowNavigator.open(DBExampleFlow())
                        }
                    ) {
                        TextR(Res.string.open_db_example)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            notifier.sendAlert(Res.string.test_dialog)
                        }
                    ) {
                        TextR(Res.string.test_dialog)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            notifier.sendMessage(Res.string.test_message)
                        }
                    ) {
                        TextR(Res.string.test_message)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            flowNavigator.close()
                        }
                    ) {
                        TextR(Res.string.close_app)
                    }
                    Spacer(Modifier.height(20.dp))
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(verticalScrollState)
                )
            }
        }
    }
}