package com.lukeneedham.vocabdrill.presentation.util.recyclerview

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Use with a RecyclerView to allow its [View.OnClickListener] to fire when clicking in the
 * 'empty' space of the view - where there are no children
 */
class RecyclerViewClickInterceptor : OnTouchListener {
    private var startX = 0f
    private var startY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (view !is RecyclerView) {
            error("RecyclerViewClickInterceptor may only be used with a RecyclerView")
        }

        var isConsumed = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                if (detectClick(startX, startY, endX, endY)) {
                    val itemView = view.findChildViewUnder(endX, endY)
                    val isItem = itemView != null
                    if (!isItem) {
                        view.performClick()
                        isConsumed = true
                    }
                }
            }
        }
        return isConsumed
    }

    private fun detectClick(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
        return abs(startX - endX) < 3.0 && abs(startY - endY) < 3.0
    }
}
