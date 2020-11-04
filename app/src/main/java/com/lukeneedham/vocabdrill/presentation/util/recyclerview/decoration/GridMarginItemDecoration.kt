package com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.util.extension.divideRoundUp

class GridMarginItemDecoration(
    private val spanCount: Int,
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
        val totalColumns = spanCount
        val column = position % spanCount
        val totalRows = itemCount.divideRoundUp(spanCount)
        val row = position / spanCount

        outRect.left = if (column == 0) edgeLeftMargin else betweenLeftMargin
        outRect.right = if (column == totalColumns - 1) edgeRightMargin else betweenRightMargin
        outRect.top = if (row == 0) edgeTopMargin else betweenTopMargin
        outRect.bottom = if (row == totalRows - 1) edgeBottomMargin else betweenBottomMargin
    }
}
