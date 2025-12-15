package ru.rznnike.demokmp.app.ui.screen.logger

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.LogMessageItem
import ru.rznnike.demokmp.app.ui.item.LogMessageServiceItem
import ru.rznnike.demokmp.app.ui.item.LogNetworkMessageItem
import ru.rznnike.demokmp.app.ui.screen.logger.network.NetworkLogDetailsScreen
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.ui.viewmodel.logger.LoggerViewModel
import ru.rznnike.demokmp.app.ui.window.LocalWindow
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class LoggerScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { LoggerViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        val window = LocalWindow.current
        val fileSaver = rememberFileSaverLauncher(
            dialogSettings = FileKitDialogSettings(
                parentWindow = window
            )
        ) { result ->
            result?.file?.let {
                viewModel.saveLogToFile(it)
            }
        }
        val coroutineScope = rememberCoroutineScope()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.One) -> viewModel.onTabSelected(LoggerViewModel.Tab.ALL)
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.Two) -> viewModel.onTabSelected(LoggerViewModel.Tab.NETWORK)
                }
            }
        }

        @Composable
        fun Header() {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                        .fillMaxWidth()
                ) {
                    SlimOutlinedTextField(
                        modifier = Modifier.width(300.dp),
                        value = viewModel.filterInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.filter)
                        },
                        onValueChange = viewModel::onFilterInput
                    )

                    @Composable
                    fun CheckboxWithText(
                        onClick: () -> Unit,
                        textRes: StringResource,
                        checked: Boolean,
                        enabled: Boolean = true
                    ) = CustomCheckboxWithText(
                        onClick = onClick,
                        checkboxSize = 18.dp,
                        contentPadding = 6.dp,
                        textRes = textRes,
                        textStyle = MaterialTheme.typography.bodySmall,
                        checked = checked,
                        enabled = enabled
                    )

                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        CheckboxWithText(
                            onClick = {
                                viewModel.onAutoscrollClick()
                            },
                            textRes = Res.string.autoscroll,
                            checked = uiState.autoscroll
                        )
                        Spacer(Modifier.height(4.dp))
                        CheckboxWithText(
                            onClick = {
                                viewModel.onShowOnlyCurrentSessionClick()
                            },
                            textRes = Res.string.show_only_current_session,
                            checked = uiState.showOnlyCurrentSession
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        CheckboxWithText(
                            onClick = {
                                viewModel.onCollapseNetworkMessagesClick()
                            },
                            textRes = Res.string.collapse_network_logs,
                            checked = uiState.collapseNetworkMessages,
                            enabled = uiState.selectedTab == LoggerViewModel.Tab.ALL
                        )
                        Spacer(Modifier.height(4.dp))
                        CheckboxWithText(
                            onClick = {
                                viewModel.onFilterOnlyByTagClick()
                            },
                            textRes = Res.string.filter_only_by_tag,
                            checked = uiState.filterOnlyByTag,
                            enabled = uiState.selectedTab == LoggerViewModel.Tab.ALL
                        )
                    }
                    Spacer(Modifier.weight(1f))

                    Spacer(Modifier.width(16.dp))
                    SelectableOutlinedIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(40.dp),
                        iconRes = Res.drawable.ic_delete,
                        onClick = {
                            viewModel.deleteLog()
                        }
                    )

                    if (uiState.selectedTab == LoggerViewModel.Tab.ALL) {
                        Spacer(Modifier.width(16.dp))
                        SelectableOutlinedIconButton(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(40.dp),
                            iconRes = Res.drawable.ic_save,
                            onClick = {
                                fileSaver.launch(
                                    suggestedName = viewModel.getSuggestedSaveFileName(),
                                    extension = DataConstants.LOG_FILE_NAME_EXTENSION
                                )
                            }
                        )
                    }
                }
            }
        }

        @Composable
        fun ColumnScope.Table() {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row {
                        Spacer(Modifier.width(12.dp))
                        LoggerViewModel.Tab.entries.forEach {
                            val tooltip = when (it) {
                                LoggerViewModel.Tab.ALL -> "Ctrl+1"
                                LoggerViewModel.Tab.NETWORK -> "Ctrl+2"
                            }
                            Tooltip(tooltip) {
                                TabText(
                                    modifier = Modifier
                                        .onClick {
                                            viewModel.onTabSelected(it)
                                        }
                                        .padding(12.dp),
                                    text = stringResource(it.nameRes),
                                    selected = it == uiState.selectedTab
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val allScrollState = rememberLazyListState()
                        val networkScrollState = rememberLazyListState()
                        val currentScrollState = when (uiState.selectedTab) {
                            LoggerViewModel.Tab.ALL -> allScrollState
                            LoggerViewModel.Tab.NETWORK -> networkScrollState
                        }
                        SelectionContainer(
                            modifier = Modifier.focusProperties {
                                canFocus = false
                            }
                        ) {
                            LazyColumn(
                                state = currentScrollState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (uiState.selectedTab) {
                                    LoggerViewModel.Tab.ALL -> {
                                        items(
                                            items = uiState.filteredLog,
                                            key = { item -> item.id }
                                        ) { message ->
                                            when (message.type) {
                                                LogType.DEFAULT,
                                                LogType.NETWORK -> {
                                                    LogMessageItem(
                                                        message = message,
                                                        query = viewModel.filterInput,
                                                        filterOnlyByTag = uiState.filterOnlyByTag,
                                                        collapseNetworkMessages = uiState.collapseNetworkMessages
                                                    )
                                                }
                                                LogType.SESSION_START -> {
                                                    LogMessageServiceItem(
                                                        type = message.type,
                                                        timestamp = message.timestamp,
                                                        isCurrentSession = message.isCurrentSession
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    LoggerViewModel.Tab.NETWORK -> {
                                        items(
                                            items = uiState.filteredNetworkLog,
                                            key = { item -> item.id }
                                        ) { message ->
                                            when (message.request.type) {
                                                LogType.NETWORK -> {
                                                    LogNetworkMessageItem(
                                                        message = message,
                                                        query = viewModel.filterInput,
                                                        onClick = {
                                                            if (BuildKonfig.DEBUG) {
                                                                navigator.openScreen(
                                                                    NetworkLogDetailsScreen(message)
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                                LogType.SESSION_START -> {
                                                    LogMessageServiceItem(
                                                        type = message.request.type,
                                                        timestamp = message.request.timestamp,
                                                        isCurrentSession = message.isCurrentSession
                                                    )
                                                }
                                                else -> Unit
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        VerticalScrollbar(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(currentScrollState)
                        )

                        val currentItems = when (uiState.selectedTab) {
                            LoggerViewModel.Tab.ALL -> uiState.filteredLog
                            LoggerViewModel.Tab.NETWORK -> uiState.filteredNetworkLog
                        }
                        suspend fun scrollToBottom() {
                            currentScrollState.scrollToItem(
                                currentItems.lastIndex.coerceAtLeast(0)
                            )
                            currentScrollState.scrollBy(
                                currentScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0f
                            )
                        }

                        val scrolledFromBottom =
                            currentScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index != currentItems.lastIndex
                        if (scrolledFromBottom) {
                            FilledButton(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(40.dp)
                                    .focusProperties {
                                        canFocus = false
                                    }
                                    .align(Alignment.BottomEnd),
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    coroutineScope.launch {
                                        scrollToBottom()
                                    }
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .size(24.dp),
                                    painter = painterResource(Res.drawable.ic_arrow_down),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = null
                                )
                            }
                        }

                        if (uiState.autoscroll) {
                            val key = when (uiState.selectedTab) {
                                LoggerViewModel.Tab.ALL -> uiState.filteredLog
                                LoggerViewModel.Tab.NETWORK -> uiState.filteredNetworkLog
                            }
                            LaunchedEffect(key) { scrollToBottom() }
                        }
                    }
                }
            }
        }

        // Main layout
        key(Locale.current) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Header()
                Spacer(Modifier.height(16.dp))
                Table()
            }
        }
    }
}