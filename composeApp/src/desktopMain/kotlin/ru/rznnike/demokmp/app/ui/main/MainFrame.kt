package ru.rznnike.demokmp.app.ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.close
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.common.notifier.SystemMessage
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.splash.SplashFlow
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider

@Preview
@Composable
fun mainFrame() {
    val notifier = koinInject<Notifier>()
    val coroutineScopeProvider = koinInject<CoroutineScopeProvider>()

    val snackbarHostState = remember { SnackbarHostState() }
    var dialogData by remember { mutableStateOf<SystemMessage?>(null) }

    fun showAlertMessage(systemMessage: SystemMessage) {
        dialogData = systemMessage
    }

    fun showBarMessage(systemMessage: SystemMessage) {
        coroutineScopeProvider.ui.launch {
            val result = snackbarHostState
                .showSnackbar(
                    message = systemMessage.text ?: "",
                    actionLabel = systemMessage.actionText?.ifBlank { null },
                    duration = SnackbarDuration.Short
                )
            if (result == SnackbarResult.ActionPerformed) {
                systemMessage.actionCallback?.invoke()
            }
        }
    }

    suspend fun onNextMessageNotify(systemMessage: SystemMessage) {
        systemMessage.text = systemMessage.textRes
            ?.let { getString(it) }
            ?: systemMessage.text
                    ?: ""
        systemMessage.actionText = systemMessage.actionTextRes
            ?.let { getString(it) }
            ?: systemMessage.actionText
                    ?: ""
        when (systemMessage.type) {
            SystemMessage.Type.ALERT -> showAlertMessage(systemMessage)
            SystemMessage.Type.BAR -> showBarMessage(systemMessage)
        }
    }

    LaunchedEffect(notifier) {
        notifier.subscribe().collect {
            onNextMessageNotify(it)
        }
    }

    @Composable
    fun DialogLayout() {
        dialogData?.let {
            Dialog(
                onDismissRequest = { dialogData = null },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Text(
                            text = dialogData?.text ?: "",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        )
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                dialogData = null
                            }
                        ) {
                            TextR(Res.string.close)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    actionColor = Color.Blue
                )
            }
        }
    ) {
        createNavigator(SplashFlow())
        DialogLayout()
    }
}
