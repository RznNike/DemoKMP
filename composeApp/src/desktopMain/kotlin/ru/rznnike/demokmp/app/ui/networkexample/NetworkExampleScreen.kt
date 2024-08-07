package ru.rznnike.demokmp.app.ui.networkexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import ru.rznnike.demokmp.app.viewmodel.networkexample.NetworkExampleViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel

class NetworkExampleScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val networkExampleViewModel = viewModel { NetworkExampleViewModel() }
        val networkExampleUiState by networkExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(Res.string.network_example_screen_title),
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Row {
                    networkExampleUiState.images.forEach {

                    }
                }

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        networkExampleViewModel.requestImages()
                    }
                ) {
                    Text(stringResource(Res.string.request_images))
                }

                Button(
                    modifier = Modifier.padding(top = 10.dp),
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