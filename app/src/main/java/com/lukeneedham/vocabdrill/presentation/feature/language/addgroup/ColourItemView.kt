package com.lukeneedham.vocabdrill.presentation.feature.language.addgroup

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable
import group.infotech.drawable.dsl.solidColor
import kotlinx.android.synthetic.main.view_item_colour.view.*

class ColourItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<Int> {

    private val shape = shapeDrawable {
        shape = GradientDrawable.OVAL
    }

    init {
        inflateFrom(R.layout.view_item_colour)
    }

    override fun setItem(position: Int, item: Int) {
        shape.solidColor = item
        colourView.background = shape
    }
}
