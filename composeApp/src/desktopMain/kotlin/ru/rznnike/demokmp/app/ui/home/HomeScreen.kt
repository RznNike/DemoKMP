package ru.rznnike.demokmp.app.ui.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.settings.SettingsFlow

class HomeScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val notifier = koinInject<Notifier>()

        val flowNavigator = getFlowNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(Res.string.main_screen_title),
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        flowNavigator.open(SettingsFlow())
                    }
                ) {
                    Text(stringResource(Res.string.open_settings))
                }

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        notifier.sendAlert(Res.string.test_dialog)
                    }
                ) {
                    Text(stringResource(Res.string.test_dialog))
                }
                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        notifier.sendMessage(Res.string.test_message)
                    }
                ) {
                    Text(stringResource(Res.string.test_message))
                }

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        flowNavigator.close()
                    }
                ) {
                    Text(stringResource(Res.string.close_app))
                }
            }
        }
    }
}