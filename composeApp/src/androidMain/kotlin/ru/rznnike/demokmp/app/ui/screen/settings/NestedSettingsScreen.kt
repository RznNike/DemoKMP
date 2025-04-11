package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.generated.resources.*

class NestedSettingsScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val profileViewModel = windowViewModel { ProfileViewModel() }

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding()
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = stringResource(Res.string.nested_settings),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val state = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .verticalScroll(state)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            SlimOutlinedTextField(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth(),
                                value = profileViewModel.nameInput,
                                singleLine = true,
                                label = {
                                    TextR(Res.string.user_name)
                                },
                                onValueChange = profileViewModel::onNameInput
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            FilledButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    navigator.closeFlow()
                                }
                            ) {
                                TextR(Res.string.to_main_screen)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}