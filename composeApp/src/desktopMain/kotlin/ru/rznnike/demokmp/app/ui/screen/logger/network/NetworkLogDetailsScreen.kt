package ru.rznnike.demokmp.app.ui.screen.logger.network

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.theme.bodyLargeBold
import ru.rznnike.demokmp.app.ui.theme.bodyMediumMono
import ru.rznnike.demokmp.app.ui.view.LinkifyText
import ru.rznnike.demokmp.app.ui.view.SelectableOutlinedIconButton
import ru.rznnike.demokmp.app.ui.view.SlimOutlinedTextField
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.window.LocalWindow
import ru.rznnike.demokmp.app.utils.backgroundColor
import ru.rznnike.demokmp.app.utils.highlightSubstrings
import ru.rznnike.demokmp.app.utils.saveFileDialog
import ru.rznnike.demokmp.app.viewmodel.logger.network.NetworkLogDetailsViewModel
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import ru.rznnike.demokmp.generated.resources.*

class NetworkLogDetailsScreen(
    private val message: LogNetworkMessage
) : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { NetworkLogDetailsViewModel(message) }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        @Composable
        fun Header() {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                ) {
                    SelectableOutlinedIconButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp),
                        iconRes = Res.drawable.ic_back,
                        onClick = {
                            navigator.closeScreen()
                        }
                    )
                }
                Spacer(Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.weight(1f),
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
                            value = viewModel.queryInput,
                            singleLine = true,
                            placeholder = {
                                TextR(Res.string.search)
                            },
                            onValueChange = viewModel::onQueryInput
                        )
                        Spacer(Modifier.width(8.dp))
                        if (uiState.queryMatches > 0) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = uiState.queryMatches.toString(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.weight(1f))

                        val clipboardManager = LocalClipboardManager.current
                        Spacer(Modifier.width(16.dp))
                        SelectableOutlinedIconButton(
                            modifier = Modifier.size(40.dp),
                            iconRes = Res.drawable.ic_copy,
                            onClick = {
                                clipboardManager.setText(AnnotatedString(viewModel.getFullText()))
                            }
                        )

                        val window = LocalWindow.current
                        val saveDialogTitle = stringResource(Res.string.save_log)
                        val saveFileNameTemplate = DataConstants.LOG_FILE_NAME_TEMPLATE
                        val saveFileName = remember {
                            saveFileNameTemplate.format(
                                uiState.logNetworkMessage
                                    .request
                                    .timestamp
                                    .toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_MS)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        SelectableOutlinedIconButton(
                            modifier = Modifier.size(40.dp),
                            iconRes = Res.drawable.ic_save,
                            onClick = {
                                saveFileDialog(
                                    window = window,
                                    title = saveDialogTitle,
                                    fileName = saveFileName
                                )?.let { file ->
                                    viewModel.saveLogToFile(file)
                                }
                            }
                        )
                    }
                }
            }
        }

        @Composable
        fun ColumnScope.Body() {
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
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(uiState.logNetworkMessage.state.backgroundColor)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        text = uiState.logNetworkMessage.request.message.lines().first().removePrefix("--> "),
                        style = MaterialTheme.typography.bodyLargeBold
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val scrollState = rememberScrollState()
                        SelectionContainer {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(
                                        state = scrollState
                                    )
                            ) {
                                @Composable
                                fun MessageText(message: LogMessage) {
                                    val text = "%s\n%s".format(
                                        message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
                                        message.getFormattedMessage()
                                    )
                                    LinkifyText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        text = text.highlightSubstrings(viewModel.queryInput),
                                        style = MaterialTheme.typography.bodyMediumMono
                                    )
                                }

                                MessageText(uiState.logNetworkMessage.request)
                                uiState.logNetworkMessage.response?.let { response ->
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    MessageText(response)
                                }
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(scrollState)
                        )
                    }
                }
            }
        }

        // Main layout
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Header()
            Spacer(Modifier.height(16.dp))
            Body()
        }
    }
}