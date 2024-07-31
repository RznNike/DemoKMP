package ru.rznnike.demokmp.app.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import demokmp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel

class SettingsScreen : NavigationScreen {
    @Preview
    @Composable
    override fun Content() {
        val screenNavigator = getScreenNavigator()
        val settingsViewModel = viewModel { SettingsViewModel() }
        val uiState by settingsViewModel.uiState.collectAsState()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(Res.string.settings_screen_title),
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = uiState.counter.toString(),
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                )
                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onClick = {
                        settingsViewModel.incrementCounter()
                    }
                ) {
                    Text("++")
                }

                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onClick = {
                        screenNavigator.open(NestedSettingsScreen())
                    }
                ) {
                    Text(stringResource(Res.string.open_nested_settings))
                }

                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onClick = {
                        screenNavigator.close()
                    }
                ) {
                    Text(stringResource(Res.string.close))
                }
            }
        }
    }
}