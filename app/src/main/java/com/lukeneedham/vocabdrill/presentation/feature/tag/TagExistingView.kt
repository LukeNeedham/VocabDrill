package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable
import kotlinx.android.synthetic.main.view_tag_existing.view.*

class TagExistingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagItemProps.Existing> {
    private val background = shapeDrawable {
        cornerRadius = context.resources.getDimension(R.dimen.tag_item_corner_radius)
    }

    init {
        inflateFrom(R.layout.view_tag_existing)
    }

    override fun setItem(position: Int, item: TagItemProps.Existing) {
        val data = item.data
        background.setColor(data.color)
        bubble.background = background
        nameView.text = data.name
        nameView.setTextColor(item.textColor)
    }
}
