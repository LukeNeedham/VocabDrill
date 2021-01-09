package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable
import group.infotech.drawable.dsl.solidColor
import kotlinx.android.synthetic.main.view_tag_suggestion.view.*

class TagSuggestionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagSuggestionItem> {

    val background = shapeDrawable {
        cornerRadius = context.resources.getDimension(R.dimen.tag_item_suggestion_corner_radius)
    }

    init {
        inflateFrom(R.layout.view_tag_suggestion)
    }

    override fun setItem(position: Int, item: TagSuggestionItem) {
        val (backgroundColor, text) = when(item) {
            is TagSuggestionItem.Create -> {
                item.color to item.name
            }
            is TagSuggestionItem.Existing -> {
                item.data.color to item.data.name
            }
        }
        background.solidColor = backgroundColor
        setBackground(background)
        textView.text = text
    }

}
