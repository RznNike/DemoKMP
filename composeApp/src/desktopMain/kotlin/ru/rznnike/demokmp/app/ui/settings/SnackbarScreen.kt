package ru.rznnike.demokmp.app.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel

class SnackbarScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val screenNavigator = getScreenNavigator()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        MaterialTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text("Show snackbar") },
                        icon = { Icon(Icons.Filled.Edit, contentDescription = "") },
                        onClick = {
                            scope.launch {
                                val result = snackbarHostState
                                    .showSnackbar(
                                        message = "Snackbar",
                                        actionLabel = "Action",
                                        // Defaults to SnackbarDuration.Short
                                        duration = SnackbarDuration.Indefinite
                                    )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        /* Handle snackbar action performed */
                                    }
                                    SnackbarResult.Dismissed -> {
                                        /* Handle snackbar dismissed */
                                    }
                                }
                            }
                        }
                    )
                }
            ) { contentPadding ->
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "QWERTY",
                        style = TextStyle(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )

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
}