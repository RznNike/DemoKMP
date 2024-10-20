package ru.rznnike.demokmp.app.ui.screen.pdfexample

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.PdfViewer
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.viewmodel.pdfexample.PdfExampleViewModel
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.pdf_example

class PdfExampleScreen : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val dispatcherProvider: DispatcherProvider = koinInject()

        val viewModel = viewModel {
            PdfExampleViewModel { command ->
                when (command) {
                    is PdfExampleViewModel.NavigationCommand.Back -> {
                        navigator.closeScreen()
                    }
                }
            }
        }
        val uiState by viewModel.uiState.collectAsState()

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.pdf_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    viewModel.onBackPressed()
                }
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                PdfViewer(
                    modifier = Modifier.fillMaxSize(),
                    file = uiState.pdf,
                    isError = uiState.isError,
                    loadingContext = dispatcherProvider.io
                )
            }
        }
    }
}