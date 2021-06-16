package com.lukeneedham.vocabdrill.presentation.util.extension

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isLastItemShowing(): Boolean? {
    val totalItems = layoutInfo.totalItemsCount
    val lastIndex = totalItems - 1
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    // There are no items showing
        ?: return null
    val lastVisibleIndex = lastVisibleItem.index
    return lastIndex == lastVisibleIndex
}
