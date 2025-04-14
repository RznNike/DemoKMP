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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openFileSaver
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.LogMessageItem
import ru.rznnike.demokmp.app.ui.item.LogNetworkMessageItem
import ru.rznnike.demokmp.app.ui.screen.logger.network.NetworkLogDetailsScreen
import ru.rznnike.demokmp.app.ui.view.SelectableOutlinedIconButton
import ru.rznnike.demokmp.app.ui.view.SlimOutlinedTextField
import ru.rznnike.demokmp.app.ui.view.TabText
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.viewmodel.logger.LoggerViewModel
import ru.rznnike.demokmp.app.ui.window.LocalWindow
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.generated.resources.*

class LoggerScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { LoggerViewModel() }
        val uiState by viewModel.uiState.collectAsState()

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
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    SlimOutlinedTextField(
                        modifier = Modifier
                            .width(300.dp)
                            .height(40.dp),
                        value = viewModel.filterInput,
                        singleLine = true,
                        placeholder = {
                            TextR(Res.string.filter)
                        },
                        onValueChange = viewModel::onFilterInput
                    )

                    @Composable
                    fun CheckboxWithText(
                        textRes: StringResource,
                        onClick: () -> Unit,
                        checked: Boolean,
                        enabled: Boolean = true
                    ) = Row(
                        modifier = Modifier.onClick {
                            if (enabled) onClick()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(
                                if (checked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off
                            ),
                            tint = when {
                                !enabled -> MaterialTheme.colorScheme.outline
                                checked -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        TextR(
                            textRes = textRes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(Modifier.width(16.dp))
                    Column {
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
                                viewModel.onFilterOnlyByTagClick()
                            },
                            textRes = Res.string.filter_only_by_tag,
                            checked = uiState.filterOnlyByTag,
                            enabled = uiState.selectedTab == LoggerViewModel.Tab.ALL
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        CheckboxWithText(
                            onClick = {
                                viewModel.onCollapseNetworkMessagesClick()
                            },
                            textRes = Res.string.collapse_network_logs,
                            checked = uiState.collapseNetworkMessages,
                            enabled = uiState.selectedTab == LoggerViewModel.Tab.ALL
                        )
                    }
                    Spacer(Modifier.weight(1f))

                    Spacer(Modifier.width(16.dp))
                    SelectableOutlinedIconButton(
                        modifier = Modifier.size(40.dp),
                        iconRes = Res.drawable.ic_delete,
                        onClick = {
                            viewModel.deleteLog()
                        }
                    )

                    if (uiState.selectedTab == LoggerViewModel.Tab.ALL) {
                        Spacer(Modifier.width(16.dp))
                        val window = LocalWindow.current
                        SelectableOutlinedIconButton(
                            modifier = Modifier.size(40.dp),
                            iconRes = Res.drawable.ic_save,
                            onClick = {
                                viewModel.openSaveLogDialog { fileName ->
                                    FileKit.openFileSaver(
                                        suggestedName = fileName,
                                        extension = DataConstants.LOG_FILE_NAME_EXTENSION,
                                        dialogSettings = FileKitDialogSettings(
                                            parentWindow = window
                                        )
                                    )
                                }
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
                            TabText(
                                modifier = Modifier
                                    .onClick {
                                        viewModel.onTabSelected(it)
                                    }
                                    .padding(12.dp),
                                textRes = it.nameRes,
                                selected = it == uiState.selectedTab
                            )
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
                        SelectionContainer {
                            LazyColumn(
                                state = currentScrollState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (uiState.selectedTab) {
                                    LoggerViewModel.Tab.ALL -> {
                                        items(
                                            items = uiState.filteredLog,
                                            key = { item -> item }
                                        ) { message ->
                                            LogMessageItem(
                                                message = message,
                                                query = viewModel.filterInput,
                                                filterOnlyByTag = uiState.filterOnlyByTag,
                                                collapseNetworkMessages = uiState.collapseNetworkMessages
                                            )
                                        }
                                    }
                                    LoggerViewModel.Tab.NETWORK -> {
                                        items(
                                            items = uiState.filteredNetworkLog,
                                            key = { item -> item.uuid }
                                        ) { message ->
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

                        if (uiState.autoscroll) {
                            when (uiState.selectedTab) {
                                LoggerViewModel.Tab.ALL -> {
                                    LaunchedEffect(uiState.filteredLog) {
                                        allScrollState.scrollToItem(
                                            uiState.filteredLog.lastIndex.coerceAtLeast(0)
                                        )
                                        allScrollState.scrollBy(
                                            allScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0f
                                        )
                                    }
                                }
                                LoggerViewModel.Tab.NETWORK -> {
                                    LaunchedEffect(uiState.filteredNetworkLog) {
                                        networkScrollState.scrollToItem(
                                            uiState.filteredNetworkLog.lastIndex.coerceAtLeast(0)
                                        )
                                        networkScrollState.scrollBy(
                                            networkScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0f
                                        )
                                    }
                                }
                            }
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