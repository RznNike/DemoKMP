package ru.rznnike.demokmp.app.ui.window.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.dispatcher.notifier.SystemMessage
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.splash.SplashFlow
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.ui.window.BackgroundBox
import ru.rznnike.demokmp.app.utils.clearFocusOnTap
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.close

@Composable
fun MainFrame() {
    val notifier = koinInject<Notifier>()
    val coroutineScopeProvider = koinInject<CoroutineScopeProvider>()

    val snackbarHostState = remember { SnackbarHostState() }
    val activeDialogs = remember { mutableStateListOf<SystemMessage>() }

    fun showAlertMessage(systemMessage: SystemMessage) {
        activeDialogs += systemMessage
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
    fun NotifierDialog() {
        fun closeDialog(dialog: SystemMessage) {
            activeDialogs -= dialog
        }

        activeDialogs.lastOrNull()?.let { dialog ->
            CommonAlertDialog(
                type = AlertDialogType.HORIZONTAL,
                header = dialog.text ?: "",
                cancellable = true,
                onCancelListener = {
                    closeDialog(dialog)
                },
                actions = listOf(
                    AlertDialogAction(stringResource(Res.string.close)) {
                        closeDialog(dialog)
                    }
                )
            )
        }
    }

    AppTheme {
        Scaffold(
            modifier = Modifier.clearFocusOnTap(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        modifier = Modifier
                            .onClick {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            },
                        snackbarData = it,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        actionColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) {
            BackgroundBox(
                modifier = Modifier.fillMaxSize()
            ) {
                createNavigator(SplashFlow())
            }
            NotifierDialog()
        }
    }
}
