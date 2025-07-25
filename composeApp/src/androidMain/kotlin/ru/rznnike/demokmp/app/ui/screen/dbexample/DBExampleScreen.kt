package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.DBExampleDataItem
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.viewmodel.dbexample.DBExampleViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class DBExampleScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { DBExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        var showToolbarMenu by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding()
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Toolbar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(Res.string.db_example),
                    leftButton = ToolbarButton(Res.drawable.ic_back) {
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
                            .weight(1f),
                        value = viewModel.nameInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.db_example_input_label)
                        },
                        onValueChange = viewModel::onNameInput,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                viewModel.addData()
                            }
                        )
                    )
                    Spacer(Modifier.width(16.dp))
                    FilledButton(
                        modifier = Modifier.padding(vertical = 16.dp),
                        onClick = {
                            viewModel.addData()
                        }
                    ) {
                        TextR(Res.string.add)
                    }
                    Spacer(Modifier.width(16.dp))
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