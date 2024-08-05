package ru.rznnike.demokmp.app.ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindow
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.close_app
import demokmp.composeapp.generated.resources.main_screen_title
import demokmp.composeapp.generated.resources.open_settings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.settings.SettingsFlow

class MainScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
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
                        flowNavigator.close()
                    }
                ) {
                    Text(stringResource(Res.string.close_app))
                }

                CommonDialog()
                Snack()
                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        // TODO
                    }
                ) {
                    Text("MESSAGE")
                }
            }
        }
    }

    @Preview
    @Composable
    fun CommonDialog() {
        var isDialogOpen by remember { mutableStateOf(false) }
        Button(onClick = { isDialogOpen = true }) {
            Text("ALERT")
        }
        if (isDialogOpen) {
            Dialog(
                onDismissRequest = { isDialogOpen = false },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Card {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Text(
                            text = "This is text",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        )
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                isDialogOpen = false
                            }
                        ) {
                            Text("OK")
                        }
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                isDialogOpen = false
                            }
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun Snack() {
        var openMySnackbar by remember { mutableStateOf(false)  }
        var snackBarMessage by remember { mutableStateOf("") }

        Column() {
            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    openMySnackbar = true
                    snackBarMessage = "Your message"
                },
            ) {
                Text(text = "Open Snackbar")
            }

            SnackbarWithoutScaffold(snackBarMessage, openMySnackbar) {
                openMySnackbar = it
            }
        }
    }

    @Composable
    fun SnackbarWithoutScaffold(
        message: String,
        showSb: Boolean,
        openSnackbar: (Boolean) -> Unit
    ) {

        val snackState = remember { SnackbarHostState() }
        val snackScope = rememberCoroutineScope()

        SnackbarHost(
            modifier = Modifier,
            hostState = snackState
        ){
            Snackbar(
                snackbarData = it,
                backgroundColor = Color.White,
                contentColor = Color.Blue
            )
        }

        if (showSb){
            LaunchedEffect(Unit) {
                snackScope.launch { snackState.showSnackbar(message) }
                openSnackbar(false)
            }

        }
    }
}