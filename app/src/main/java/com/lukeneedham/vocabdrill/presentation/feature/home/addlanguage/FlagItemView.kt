package com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.Flag
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_flag.view.*

class FlagItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<Flag> {
    init {
        inflateFrom(R.layout.view_item_flag)
    }

    override fun setItem(position: Int, item: Flag) {
        val flag = item.drawable
        flagView.setImageDrawable(flag)
    }
}
