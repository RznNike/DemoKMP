package ru.rznnike.demokmp.app.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.common.*
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import ru.rznnike.demokmp.domain.model.chart.ChartPoint

@Composable
fun getCustomVicoTheme() = vicoTheme.copy(
    lineColor = MaterialTheme.colorScheme.outline,
    textColor = MaterialTheme.colorScheme.onBackground
)

@Composable
fun rememberGradientLineProvider(): LineCartesianLayer.LineProvider {
    val lineColor = MaterialTheme.colorScheme.primary
    val areaColor = lineColor.copy(alpha = 0.4f)
    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
        areaFill = LineCartesianLayer.AreaFill.single(
            fill = Fill(Brush.verticalGradient(listOf(areaColor, Color.Transparent, areaColor)))
        )
    )
    return remember {
        LineCartesianLayer.LineProvider.series(line)
    }
}

@Composable
fun <M : MeasuringContext, D : DrawingContext> rememberSimpleHorizontalLegend(
    items: List<Pair<Color, String>>,
    padding: Insets = Insets.Zero
): HorizontalLegend<M, D> {
    val legendItemLabelComponent = rememberTextComponent(
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground
        )
    )
    val legendShape = MaterialTheme.shapes.extraSmall
    return rememberHorizontalLegend(
        items = {
            items.forEach { (color, text) ->
                add(
                    LegendItem(
                        icon = ShapeComponent(
                            fill = Fill(color),
                            shape = legendShape
                        ),
                        labelComponent = legendItemLabelComponent,
                        label = text
                    )
                )
            }
        },
        padding = padding
    )
}

@Composable
fun rememberChartPointProducer(data: List<ChartPoint>): CartesianChartModelProducer {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(data) {
        val finalData = if (data.isEmpty()) listOf(ChartPoint()) else data
        modelProducer.runTransaction {
            lineModel {
                series(
                    x = finalData.map { it.x },
                    y = finalData.map { it.y }
                )
            }
        }
    }
    return modelProducer
}