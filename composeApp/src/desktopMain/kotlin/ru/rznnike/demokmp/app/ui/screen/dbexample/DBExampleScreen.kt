package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.DBExampleDataItem
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.app.viewmodel.dbexample.DBExampleViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class DBExampleScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { DBExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        var showToolbarMenu by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Toolbar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(Res.string.db_example),
                    leftButton = ToolbarButton(
                        iconRes = Res.drawable.ic_back,
                        tooltip = "Ctrl+W"
                    ) {
                        navigator.closeScreen()
                    },
                    rightButton = ToolbarButton(Res.drawable.ic_menu) {
                        showToolbarMenu = true
                    }
                )
                Box {
                    DropdownMenu(
                        expanded = showToolbarMenu,
                        onDismissRequest = { showToolbarMenu = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DropdownMenuItem(
                            text = {
                                TextR(Res.string.delete_all)
                            },
                            onClick = {
                                showToolbarMenu = false
                                viewModel.deleteAllData()
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(16.dp))
                    SlimOutlinedTextField(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 16.dp)
                            .weight(1f)
                            .onEnterKey {
                                viewModel.addData()
                            },
                        value = viewModel.nameInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.db_example_input_label)
                        },
                        onValueChange = viewModel::onNameInput
                    )
                    Spacer(Modifier.width(12.dp))
                    SelectableButton(
                        modifier = Modifier.padding(vertical = 12.dp),
                        onClick = {
                            viewModel.addData()
                        }
                    ) {
                        TextR(Res.string.add)
                    }
                    Spacer(Modifier.width(12.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        state = state,
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.data,
                            key = { it.id }
                        ) { item ->
                            DBExampleDataItem(item) {
                                viewModel.deleteData(item)
                            }
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(state)
                    )

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}