package ru.rznnike.demokmp.app.utils

import androidx.compose.foundation.lazy.LazyListState

internal data class VisibleItem(
    val index: Int,
    val offset: Int,
    val size: Int,
)

internal data class ScrollTarget(
    val index: Int,
    val offset: Int,
)

internal fun computeScrollTarget(
    targetIndex: Int,
    totalItemsCount: Int,
    firstVisibleItemIndex: Int,
    lastVisibleItem: VisibleItem?,
    viewportHeight: Int,
): ScrollTarget? {
    if (totalItemsCount == 0) return null

    val safeItemIndex = targetIndex.coerceIn(0, totalItemsCount - 1)

    if (firstVisibleItemIndex > safeItemIndex) return ScrollTarget(index = safeItemIndex, offset = 0)

    val lastVisibleItemOffset = lastVisibleItem?.let {
        it.offset + it.size - viewportHeight
    } ?: 0
    val lastIndexCorrection = if (lastVisibleItemOffset < 0) 0 else 1
    val lastVisibleIndex = (lastVisibleItem?.index ?: 0) - lastIndexCorrection

    if (lastVisibleIndex >= safeItemIndex) return null

    val itemHeight = lastVisibleItem?.size ?: 0
    return ScrollTarget(index = safeItemIndex, offset = itemHeight - viewportHeight)
}

suspend fun LazyListState.smartScrollToItem(index: Int) = computeScrollTarget(
    targetIndex = index,
    totalItemsCount = layoutInfo.totalItemsCount,
    firstVisibleItemIndex = firstVisibleItemIndex,
    lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
        ?.let { VisibleItem(it.index, it.offset, it.size) },
    viewportHeight = layoutInfo.viewportSize.height,
)?.let { target ->
    scrollToItem(target.index, target.offset)
}
