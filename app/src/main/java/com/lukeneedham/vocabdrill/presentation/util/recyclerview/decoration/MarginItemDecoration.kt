package com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class MarginItemDecoration : RecyclerView.ItemDecoration() {

    protected abstract val edgeTopMargin: Int
    protected abstract val edgeBottomMargin: Int
    protected abstract val edgeLeftMargin: Int
    protected abstract val edgeRightMargin: Int
    protected abstract val betweenTopMargin: Int
    protected abstract val betweenBottomMargin: Int
    protected abstract val betweenLeftMargin: Int
    protected abstract val betweenRightMargin: Int

    abstract fun getOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
        position: Int,
        itemCount: Int
    )

    final override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter ?: error("Recycler adapter must be set")
        val position = parent.getChildAdapterPosition(view)
        val itemCount = adapter.itemCount
        getOffsets(outRect, view, parent, state, position, itemCount)
    }

    final override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
    }
}
