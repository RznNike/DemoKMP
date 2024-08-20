package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel

class SettingsScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val settingsViewModel = viewModel { SettingsViewModel() }
        val settingsUiState by settingsViewModel.uiState.collectAsState()
        val profileViewModel: ProfileViewModel = koinInject()

        val screenNavigator = getScreenNavigator()

        Column(modifier = Modifier.padding(20.dp)) {
            TextR(
                textRes = Res.string.settings_screen_title,
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = settingsUiState.counter.toString(),
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    settingsViewModel.incrementCounter()
                }
            ) {
                Text("++")
            }

            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    screenNavigator.open(NestedSettingsScreen())
                }
            ) {
                TextR(Res.string.open_nested_settings)
            }

            val nameString = "%s: %s".format(
                stringResource(Res.string.user_name),
                profileViewModel.nameInput
            )
            Text(
                text = nameString,
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    screenNavigator.close()
                }
            ) {
                TextR(Res.string.close)
            }
        }
    }
}