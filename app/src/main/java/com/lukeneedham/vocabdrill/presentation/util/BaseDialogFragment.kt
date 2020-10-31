package com.lukeneedham.vocabdrill.presentation.util

import android.graphics.Point
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * Base [DialogFragment] which inflates [layoutResId],
 * removes the default dialog background, and sets size as requested by [getSize]
 */
abstract class BaseDialogFragment : DialogFragment() {
    abstract val layoutResId: Int

    abstract fun getSize(windowSize: Size): Size

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = requireWindow()
        window.setBackgroundDrawableResource(android.R.color.transparent)
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onResume() {
        super.onResume()

        val window = requireWindow()
        val point = Point()
        val display = window.windowManager?.defaultDisplay
        display?.getSize(point)
        val size = getSize(Size(point.x, point.y))
        window.setLayout(size.width, size.height)
    }

    private fun requireWindow() = requireDialog().window ?: error("No window set")
}
