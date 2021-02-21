package com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_tag_suggestions_pop_up.view.*

class TagSuggestionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val adapter =
        SingleTypeRecyclerAdapterCreator.fromRecyclerItemView<TagSuggestion, TagSuggestionItemView>(
            SingleTypeAdapterConfig<TagSuggestion, TagSuggestionItemView>().apply {
                addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                addOnItemClickListener { item, _, _ ->
                    onSuggestionClickListener(item)
                }
            }
        )

    lateinit var onSuggestionClickListener: (TagSuggestion) -> Unit

    init {
        inflateFrom(R.layout.view_tag_suggestions_pop_up)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    fun setSuggestions(tags: List<TagSuggestion>) {
        adapter.submitList(tags)
        noSuggestionsText.visibility = if (tags.isEmpty()) View.VISIBLE else View.GONE
    }
}
