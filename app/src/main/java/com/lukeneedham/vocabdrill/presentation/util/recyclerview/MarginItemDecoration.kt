package com.lukeneedham.vocabdrill.presentation.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class MarginItemDecoration(
) : RecyclerView.ItemDecoration() {

    protected abstract val firstVerticalMargin: Int
    protected abstract  val betweenVerticalMargin: Int
    protected abstract  val lastVerticalMargin: Int
    protected abstract  val firstHorizontalMargin: Int
    protected abstract  val betweenHorizontalMargin: Int
    protected abstract  val lastHorizontalMargin: Int

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter ?: return
        val position = parent.getChildAdapterPosition(view)

        outRect.top =
            if (position == 0) firstVerticalMargin else betweenVerticalMargin
        outRect.bottom =
            if (position == adapter.itemCount - 1) lastVerticalMargin else betweenVerticalMargin

        outRect.left =
            if (position == 0) firstHorizontalMargin else betweenHorizontalMargin
        outRect.right =
            if (position == adapter.itemCount - 1) lastHorizontalMargin else betweenHorizontalMargin
    }
}
