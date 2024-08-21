package ru.rznnike.demokmp.app.ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.close
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.common.notifier.Notifier
import ru.rznnike.demokmp.app.common.notifier.SystemMessage
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.splash.SplashFlow
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.model.common.Theme

@Preview
@Composable
fun mainFrame() {
    val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
    val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

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
    fun DialogLayout() {
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

    val darkTheme = when (appConfigurationUiState.theme) {
        Theme.AUTO -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }
    AppTheme(
        darkTheme = darkTheme
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        snackbarData = it,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        actionColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        ) {
            createNavigator(SplashFlow())
            DialogLayout()
        }
    }
}
