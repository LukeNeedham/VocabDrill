package com.lukeneedham.vocabdrill.presentation.util.extension

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.computeVerticalScrollPercent(): Float {
    val offset: Int = computeVerticalScrollOffset()
    val extent: Int = computeVerticalScrollExtent()
    val range: Int = computeVerticalScrollRange()
    val total = (range - extent).toFloat()
    if(total == 0f) {
        return 0f
    }
    val percentage = offset / total
    return percentage
}
