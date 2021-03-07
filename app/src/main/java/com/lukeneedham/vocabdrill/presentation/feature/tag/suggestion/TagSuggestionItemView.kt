package com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable
import group.infotech.drawable.dsl.solidColor
import kotlinx.android.synthetic.main.view_tag_suggestion.view.*

class TagSuggestionItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagSuggestion> {

    val background = shapeDrawable {
        cornerRadius = context.resources.getDimension(R.dimen.tag_item_suggestion_corner_radius)
    }

    init {
        inflateFrom(R.layout.view_tag_suggestion)
    }

    override fun setItem(position: Int, item: TagSuggestion) {
        background.solidColor = item.color
        backgroundView.setBackground(background)
        textView.text = item.name
        textView.setTextColor(item.textColor)
    }

}
