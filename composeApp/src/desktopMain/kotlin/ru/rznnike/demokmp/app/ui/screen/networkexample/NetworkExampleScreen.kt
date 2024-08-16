package ru.rznnike.demokmp.app.ui.screen.networkexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.close
import demokmp.composeapp.generated.resources.network_example_screen_title
import demokmp.composeapp.generated.resources.request_images
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.networkexample.NetworkExampleViewModel

class NetworkExampleScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val networkExampleViewModel = viewModel { NetworkExampleViewModel() }
        val networkExampleUiState by networkExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        Column(modifier = Modifier.padding(20.dp)) {
            TextR(
                textRes = Res.string.network_example_screen_title,
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.padding(top = 10.dp).height(200.dp).fillMaxWidth()
            ) {
                networkExampleUiState.images.forEach { image ->
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(end = 10.dp),
                        model = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator()
                        }
                    )
                }
            }

            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    networkExampleViewModel.requestImages()
                }
            ) {
                TextR(Res.string.request_images)
            }

            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    screenNavigator.close()
                }
            ) {
                TextR(Res.string.close)
            }
        }
    }
}