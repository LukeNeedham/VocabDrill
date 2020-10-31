package com.lukeneedham.vocabdrill.presentation.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearMarginItemDecoration(
    override val edgeTopMargin: Int,
    override val edgeBottomMargin: Int,
    override val edgeLeftMargin: Int,
    override val edgeRightMargin: Int,
    override val betweenTopMargin: Int,
    override val betweenBottomMargin: Int,
    override val betweenLeftMargin: Int,
    override val betweenRightMargin: Int
) : MarginItemDecoration() {

    override fun getOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
        position: Int,
        itemCount: Int
    ) {
        outRect.top = if (position == 0) edgeTopMargin else betweenTopMargin
        outRect.bottom = if (position == itemCount - 1) edgeBottomMargin else betweenBottomMargin
        outRect.left = if (position == 0) edgeLeftMargin else betweenLeftMargin
        outRect.right = if (position == itemCount - 1) edgeRightMargin else betweenRightMargin
    }
}
