package ru.rznnike.demokmp.app.ui.screen.httpexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.http_example
import demokmp.composeapp.generated.resources.ic_back
import demokmp.composeapp.generated.resources.ic_refresh
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.ui.theme.localCustomColorScheme
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.viewmodel.httpexample.HTTPExampleViewModel

class HTTPExampleScreen : NavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Preview
    @Composable
    override fun Content() {
        val httpExampleViewModel = viewModel { HTTPExampleViewModel() }
        val httpExampleUiState by httpExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.http_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    screenNavigator.close()
                },
                rightButton = ToolbarButton(Res.drawable.ic_refresh) {
                    httpExampleViewModel.requestImages()
                }
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val verticalScrollState = rememberScrollState()
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(verticalScrollState)
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    httpExampleUiState.images.forEach { image ->
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(MaterialTheme.shapes.medium),
                            model = image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(16.dp)
                                )
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

                if (httpExampleUiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(localCustomColorScheme.current.surfaceContainerA50),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
}