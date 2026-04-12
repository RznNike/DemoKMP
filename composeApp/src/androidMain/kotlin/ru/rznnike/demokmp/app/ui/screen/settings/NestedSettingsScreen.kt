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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.navigationBarsSidesPadding
import ru.rznnike.demokmp.app.utils.statusBarsAndCutoutPadding
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.profile.ProfileViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class NestedSettingsScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val profileViewModel = windowViewModel<ProfileViewModel>()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .statusBarsAndCutoutPadding()
        ) {
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsSidesPadding(),
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
                        .fillMaxSize()
                        .verticalScroll(state)
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(bottom = 16.dp)
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
                                    Text(Res.string.user_name)
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
                                Text(Res.string.to_main_screen)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}