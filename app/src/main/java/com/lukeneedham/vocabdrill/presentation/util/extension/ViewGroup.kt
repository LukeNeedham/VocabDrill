package com.lukeneedham.vocabdrill.presentation.util.extension

import android.view.View.inflate
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflateFrom(@LayoutRes layoutResId: Int) {
    inflate(context, layoutResId, this)
}
