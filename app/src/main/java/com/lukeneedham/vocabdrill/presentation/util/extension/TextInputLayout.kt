// Lie about the package, so we can access package-private fields
package com.google.android.material.textfield

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams

fun TextInputLayout.setHintEnabledMaintainMargin(enabled: Boolean) {
    isHintEnabled = enabled
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        // If hint is enabled, [TextInputLayout] will handle the margin itself.
        // Otherwise, we have to set it manually
        topMargin = if (enabled) 0 else getHintMarginTop()
    }
}

/** @return the top margin used when showing a hint */
@SuppressLint("RestrictedApi")
private fun TextInputLayout.getHintMarginTop(): Int {
    return when (boxBackgroundMode) {
        TextInputLayout.BOX_BACKGROUND_OUTLINE ->
            (collapsingTextHelper.collapsedTextHeight / 2).toInt()
        TextInputLayout.BOX_BACKGROUND_FILLED,
        TextInputLayout.BOX_BACKGROUND_NONE ->
            collapsingTextHelper.collapsedTextHeight.toInt()
        else -> error("Unsupported background mode: $boxBackgroundMode")
    }
}
