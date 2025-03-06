package ru.rznnike.demokmp.app.ui.screen.httpexample

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.viewmodel.httpexample.HTTPExampleViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.http_example
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.ic_refresh

class HTTPExampleScreen : NavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { HTTPExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.http_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                },
                rightButton = ToolbarButton(Res.drawable.ic_refresh) {
                    viewModel.requestImages()
                }
            )
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val verticalScrollState = rememberScrollState()
                    FlowRow(
                        modifier = Modifier
                            .verticalScroll(verticalScrollState)
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        uiState.images.forEach { image ->
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.background),
                                model = image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .align(Alignment.Center)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(verticalScrollState)
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