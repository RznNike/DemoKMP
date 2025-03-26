package ru.rznnike.demokmp.app.ui.activity

import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowCompat
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
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.app.utils.restartApp
import ru.rznnike.demokmp.app.viewmodel.app.ActivityViewModel
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.close
import ru.rznnike.demokmp.R
import ru.rznnike.demokmp.app.ui.window.LocalWindowCloseCallback
import ru.rznnike.demokmp.app.utils.AppConstants
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.generated.resources.double_back_to_exit
import kotlin.system.exitProcess

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindowFlags()
        updateTaskDescription()

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
        viewModel {
            ActivityViewModel {
                restartApp()
            }
        }

        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()

        LaunchedEffect("init") {
            appConfigurationViewModel.setCloseAppCallback {
                exitProcess(0)
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

        var doubleBackToExitPressedOnce by remember { mutableStateOf(false) }
        val onBackPressedCallback: () -> Unit = remember {
            {
                if (doubleBackToExitPressedOnce) {
                    appConfigurationViewModel.closeApplication()
                } else {
                    doubleBackToExitPressedOnce = true
                    notifier.sendMessage(Res.string.double_back_to_exit)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { doubleBackToExitPressedOnce = false },
                        AppConstants.APP_EXIT_DURATION_MS
                    )
                }
            }
        }
        CompositionLocalProvider(
            LocalWindowCloseCallback provides onBackPressedCallback
        ) {
            AppTheme {
                val focusManager = LocalFocusManager.current
                Scaffold(
                    modifier = Modifier.onClick {
                        focusManager.clearFocus()
                    },
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
                    createNavigator(SplashFlow())
                    NotifierDialog()
                }
            }
        }
    }

    private fun initWindowFlags() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun updateTaskDescription() {
        val taskDesc = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ActivityManager.TaskDescription.Builder()
                    .setLabel(resources.getString(R.string.window_title))
                    .setIcon(R.mipmap.ic_launcher)
                    .setPrimaryColor(getColor(R.color.colorBackground))
                    .build()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(
                    resources.getString(R.string.window_title),
                    R.mipmap.ic_launcher,
                    getColor(R.color.colorBackground)
                )
            }
            else -> {
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(
                    resources.getString(R.string.window_title),
                    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
                    getColor(R.color.colorBackground)
                )
            }
        }

        setTaskDescription(taskDesc)
    }
}
