package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

@Composable
fun ImageGallery(
    showGallery: MutableState<Boolean>,
    items: List<String>,
    startIndex: Int = 0
) {
    if (showGallery.value) {
        Dialog(
            onDismissRequest = { showGallery.value = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val pagerState = rememberPagerState(
                initialPage = startIndex
            ) { items.size }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
                overscrollEffect = null
            ) { page ->
                ZoomableAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberZoomableImageState(
                        rememberZoomableState(
                            zoomSpec = ZoomSpec(
                                minZoomFactor = 0.8f,
                                maxZoomFactor = 2f
                            )
                        )
                    ),
                    model = items[page],
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    onClick = { showGallery.value = false }
                )
            }
        }
    }
}