package ru.rznnike.demokmp.app.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.dispatcher.notifier.SystemMessage
import ru.rznnike.demokmp.app.navigation.createNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.splash.SplashFlow
import ru.rznnike.demokmp.app.ui.theme.AppTheme
import ru.rznnike.demokmp.app.utils.restartApp
import ru.rznnike.demokmp.app.viewmodel.app.ActivityViewModel
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.close

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinContext {
                ProvideNavigatorLifecycleKMPSupport {
                    MainFrame()
                }
            }
        }
    }

    @Composable
    fun MainFrame() {
        val viewModel = viewModel {
            ActivityViewModel {
                restartApp()
            }
        }

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
                ?.let { org.jetbrains.compose.resources.getString(it) }
                ?: systemMessage.text
                        ?: ""
            systemMessage.actionText = systemMessage.actionTextRes
                ?.let { org.jetbrains.compose.resources.getString(it) }
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
            val focusManager = LocalFocusManager.current
            Scaffold(
                modifier = Modifier.clickable {
                    focusManager.clearFocus()
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        Snackbar(
                            modifier = Modifier
                                .clickable {
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
                createNavigator(SplashFlow())
                NotifierDialog()
            }
        }
    }
}
