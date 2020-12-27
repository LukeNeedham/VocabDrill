package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable

class TagView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagItem.Existing> {
    private val background = shapeDrawable {
        // TODO: Use dimen
        cornerRadius = 10f
    }

    init {
        inflateFrom(R.layout.view_tag)
    }

    override fun setItem(position: Int, item: TagItem.Existing) {
        val data = item.data
        background.setColor(data.colour)
        setBackground(background)
    }
}
