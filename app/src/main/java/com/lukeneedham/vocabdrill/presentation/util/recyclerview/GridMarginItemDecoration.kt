package com.lukeneedham.vocabdrill.presentation.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridMarginItemDecoration(
    private val spanCount: Int,
    private val margin: Int,
    private val includeEdge: Boolean
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        if (includeEdge) {
            outRect.left =
                margin - column * margin / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * margin / spanCount // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = margin
            }
            outRect.bottom = margin // item bottom
        } else {
            outRect.left = column * margin / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                margin - (column + 1) * margin / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = margin // item top
            }
        }
    }
}
