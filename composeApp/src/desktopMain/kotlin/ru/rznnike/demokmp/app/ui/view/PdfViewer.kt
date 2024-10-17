package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_minus
import ru.rznnike.demokmp.generated.resources.ic_plus
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

private const val SCALE_MIN = 0.3f
private const val SCALE_MAX = 1f
private const val SCALE_STEP = 0.1f
private val SCROLL_STEP_DP = 100.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    file: File?,
    dpi: Float = 300f,
    loadingContext: CoroutineContext = Dispatchers.IO
) = Box(
    modifier = modifier
) {
    var pages by remember { mutableStateOf(listOf<ImageBitmap>()) }
    var scale by remember { mutableStateOf(1f) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val scrollStepPx = with(LocalDensity.current) {
        SCROLL_STEP_DP.toPx()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Scroll) { event ->
                val change = event.changes.first()
                val scrollDelta = change.scrollDelta.y
                if (event.keyboardModifiers.isCtrlPressed) {
                    scale = (scale - scrollDelta * SCALE_STEP).coerceIn(SCALE_MIN, SCALE_MAX)
                } else {
                    coroutineScope.launch {
                        scrollState.scrollBy(scrollDelta * scrollStepPx)
                    }
                }
                change.consume()
            },
        userScrollEnabled = false,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp, start = 16.dp, end = 16.dp)
    ) {
        items(pages) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(scale)
                    .wrapContentHeight()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                bitmap = it,
                contentDescription = null
            )
        }
    }
    VerticalScrollbar(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
    if (pages.isNotEmpty()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            @Composable
            fun ZoomButton(
                iconRes: DrawableResource,
                onClick: () -> Unit
            ) = Button(
                modifier = Modifier
                    .size(40.dp)
                    .focusProperties {
                        canFocus = false
                    },
                contentPadding = PaddingValues(0.dp),
                onClick = onClick
            ) {
                Icon(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(24.dp),
                    painter = painterResource(iconRes),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null
                )
            }

            ZoomButton(Res.drawable.ic_minus) {
                scale = max(scale - SCALE_STEP, SCALE_MIN)
            }
            Spacer(Modifier.width(16.dp))
            ZoomButton(Res.drawable.ic_plus) {
                scale = min(scale + SCALE_STEP, SCALE_MAX)
            }
        }
    }

    LaunchedEffect(file) {
        launch(loadingContext) {
            pages = file?.let {
                Loader.loadPDF(file).use { pdf ->
                    val renderer = PDFRenderer(pdf)
                    (0 until pdf.numberOfPages).map { index ->
                        renderer.renderImageWithDPI(index, dpi).toComposeImageBitmap()
                    }
                }
            } ?: emptyList()
        }
    }
}