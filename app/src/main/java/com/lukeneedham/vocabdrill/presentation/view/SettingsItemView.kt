package com.lukeneedham.vocabdrill.presentation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_settings.view.*

class SettingsItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflateFrom(R.layout.view_item_settings)
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setText(textRes: Int) {
        textView.setText(textRes)
    }

    fun setIcon(drawable: Drawable?) {
        iconView.setImageDrawable(drawable)
    }

    fun setIcon(drawableRes: Int) {
        iconView.setImageResource(drawableRes)
    }
}
