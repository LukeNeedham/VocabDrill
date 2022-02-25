package com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_tag_suggestions.view.*

class TagSuggestionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val adapter =
        SingleTypeRecyclerAdapterCreator.fromRecyclerItemView<TagSuggestion, TagSuggestionItemView>(
            SingleTypeAdapterConfig<TagSuggestion, TagSuggestionItemView>().apply {
                addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
                addOnItemClickListener { item, _, _ ->
                    onSuggestionClickListener(item)
                }
            }
        )

    lateinit var onSuggestionClickListener: (TagSuggestion) -> Unit

    init {
        inflateFrom(R.layout.view_tag_suggestions)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        recyclerView.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }
    }

    /**
     * @param tags can be:
     * - a list of tags to show
     * - an empty list of tags, representing that no suggestions were found
     * - null, representing that the suggestions are loading
     */
    fun setSuggestions(tags: List<TagSuggestion>?) {

        /**
         * Toggle whether [recyclerView] is visible.
         * It does not use [View.setVisibility] or [View.setAlpha],
         * because doing so will prevent recyclerView children from being updated,
         * and this creates a flicker.
         *
         * Instead, this workaround makes the recyclerView too small to be seen,
         * while 'tricking' the Android system into thinking the view is still visible,
         * so allows View updates to continue as necessary.
         */
        fun toggleRecyclerView(shouldShow: Boolean) {
            recyclerView.updateLayoutParams {
                // Use 1 as the 'invisible' height - it's the smallest height available to us
                height = if(shouldShow) MATCH_CONSTRAINT else 1
            }
        }

        if (tags == null) {
            // Loading - neither is visible
            toggleRecyclerView(false)
            noSuggestionsText.visibility = View.GONE
            adapter.submitList(emptyList())
            return
        }

        if (tags.isEmpty()) {
            // Loaded, and result is that there are no suggestions
            toggleRecyclerView(false)
            noSuggestionsText.visibility = View.VISIBLE
            adapter.submitList(emptyList())
            return
        }

        // Only show recyclerview after tags are updated, to prevent flicker
        adapter.submitList(tags) {
            recyclerView.post {
                toggleRecyclerView(true)
                noSuggestionsText.visibility = View.GONE
            }
        }
    }
}
