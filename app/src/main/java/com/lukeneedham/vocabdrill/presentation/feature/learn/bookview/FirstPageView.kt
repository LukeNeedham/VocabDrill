package com.lukeneedham.vocabdrill.presentation.feature.learn.bookview

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_flip_book_page_first.view.*

class FirstPageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflateFrom(R.layout.view_flip_book_page_first)
    }

    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }

    fun setText(text: CharSequence?) {
        textView.text = text
    }
}
