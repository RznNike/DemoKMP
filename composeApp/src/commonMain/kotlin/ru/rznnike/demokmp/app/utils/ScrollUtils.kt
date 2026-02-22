package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.lazy.LazyListState

suspend fun LazyListState.smartScrollToItem(index: Int) {
    if (layoutInfo.totalItemsCount == 0) return

    val safeItemIndex = index.coerceIn(0, layoutInfo.totalItemsCount - 1)

    if (firstVisibleItemIndex > safeItemIndex) {
        scrollToItem(safeItemIndex)
    } else {
        val itemHeight = layoutInfo.visibleItemsInfo.lastOrNull()?.size ?: 0
        val lastVisibleItemOffset = layoutInfo.visibleItemsInfo.lastOrNull()?.let {
            it.offset + itemHeight - layoutInfo.viewportSize.height
        } ?: 0
        val lastIndexCorrection = if (lastVisibleItemOffset < 0) 0 else 1
        val lastVisibleIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) - lastIndexCorrection
        if (lastVisibleIndex < (safeItemIndex)) {
            scrollToItem(
                index = safeItemIndex,
                scrollOffset = itemHeight - layoutInfo.viewportSize.height
            )
        }
    }
}