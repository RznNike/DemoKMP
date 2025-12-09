package ru.rznnike.demokmp.app.ui.screen.chartexample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.Zoom
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.*
import com.patrykandpatrick.vico.multiplatform.common.component.ShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.getCustomVicoTheme
import ru.rznnike.demokmp.app.viewmodel.chartexample.ChartExampleViewModel
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.chart_example
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.test_data

@Serializable
class ChartExampleScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { ChartExampleViewModel() }
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
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.chart_example),
                leftButton = ToolbarButton(
                    iconRes = Res.drawable.ic_back,
                    tooltip = "Ctrl+W"
                ) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val modelProducer = remember { CartesianChartModelProducer() }
                    LaunchedEffect(uiState.data) {
                        modelProducer.runTransaction {
                            lineSeries {
                                if (uiState.data.isNotEmpty()) {
                                    series(
                                        x = uiState.data.map { it.x },
                                        y = uiState.data.map { it.y }
                                    )
                                } else {
                                    series(
                                        x = listOf(0),
                                        y = listOf(0)
                                    )
                                }
                            }
                        }
                    }
                    val lineColor = MaterialTheme.colorScheme.primary
                    val areaColor = lineColor.copy(alpha = 0.4f)
                    val legendItemLabelComponent = rememberTextComponent(
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    val legendLineLabel = stringResource(Res.string.test_data)
                    val legendShape = MaterialTheme.shapes.extraSmall
                    ProvideVicoTheme(getCustomVicoTheme()) {
                        CartesianChartHost(
                            modifier = Modifier.fillMaxSize(),
                            chart = rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lineProvider = LineCartesianLayer.LineProvider.series(
                                        LineCartesianLayer.rememberLine(
                                            fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                                            areaFill = LineCartesianLayer.AreaFill.single(
                                                fill = Fill(Brush.verticalGradient(listOf(areaColor, Color.Transparent, areaColor)))
                                            )
                                        )
                                    )
                                ),
                                startAxis = VerticalAxis.rememberStart(),
                                bottomAxis = HorizontalAxis.rememberBottom(),
                                legend = rememberHorizontalLegend(
                                    items = {
                                        add(
                                            LegendItem(
                                                icon = ShapeComponent(
                                                    fill = Fill(lineColor),
                                                    shape = legendShape
                                                ),
                                                labelComponent = legendItemLabelComponent,
                                                label = legendLineLabel
                                            )
                                        )
                                    },
                                    padding = Insets(16.dp)
                                )
                            ),
                            modelProducer = modelProducer,
                            zoomState = rememberVicoZoomState(
                                initialZoom = Zoom.Content
                            )
                        )
                    }
                }
            }
        }
    }
}