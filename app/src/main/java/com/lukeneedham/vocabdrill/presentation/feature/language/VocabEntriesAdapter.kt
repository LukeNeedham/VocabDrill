package com.lukeneedham.vocabdrill.presentation.feature.language

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItemProps
import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryViewProps
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateProps
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingProps
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.ListDiffCallback
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.ViewHolder

class VocabEntriesAdapter(
    private val vocabEntryExistingCallback: VocabEntryExistingCallback,
    private val vocabEntryCreateCallback: VocabEntryCreateCallback,
    private val tagCreateCallback: TagCreateCallback,
    private val tagExistingClickListener: (entryItem: VocabEntryEditItem, tag: TagItemProps.Existing) -> Unit,
    private val tagSuggestionClickListener: (entryItem: VocabEntryEditItem, suggestion: TagSuggestion) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<VocabEntryViewProps> = emptyList()

    init {
        setHasStableIds(true)
    }

    /**
     * @param refresh controls whether to rebind for this new data, overriding item view internal state.
     * If [refresh] is false, internal state of the item view will be preserved instead.
     * This is an optimisation - by passing [refresh] as false when internal item view state is reliable,
     * unnecessary rebinds can be avoided and performance improved.
     */
    fun submitList(items: List<VocabEntryViewProps>, refresh: Boolean) {
        val oldItems = this.items
        val newItems = items
        this.items = items
        if (refresh) {
            val diffCallback = ListDiffCallback(oldItems, newItems, diffItemCallback)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            ViewType.Existing -> VocabEntryExistingItemView(parent.context).apply {
                this.vocabEntryExistingCallback =
                    this@VocabEntriesAdapter.vocabEntryExistingCallback
                this.tagCreateCallback = this@VocabEntriesAdapter.tagCreateCallback
                this.tagExistingClickListener = this@VocabEntriesAdapter.tagExistingClickListener
                this.tagSuggestionClickListener =
                    this@VocabEntriesAdapter.tagSuggestionClickListener
            }
            ViewType.Create -> VocabEntryCreateItemView(parent.context).apply {
                this.vocabEntryCreateCallback = this@VocabEntriesAdapter.vocabEntryCreateCallback
                this.tagCreateCallback = this@VocabEntriesAdapter.tagCreateCallback
                this.tagExistingClickListener = this@VocabEntriesAdapter.tagExistingClickListener
                this.tagSuggestionClickListener =
                    this@VocabEntriesAdapter.tagSuggestionClickListener
            }
            else -> error("Unhandled viewType: $viewType")
        }
        view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemAt(position)
        val itemView = holder.itemView
        when (item) {
            is VocabEntryExistingProps ->
                (itemView as VocabEntryExistingItemView).setItem(position, item)
            is VocabEntryCreateProps ->
                (itemView as VocabEntryCreateItemView).setItem(position, item)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long {
        val item = getItemAt(position)
        return when (item) {
            is VocabEntryExistingProps -> item.entryItem.entry.id
            is VocabEntryCreateProps -> Long.MAX_VALUE
            else -> error("Unexpected item type: $item")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItemAt(position)
        return when (item) {
            is VocabEntryExistingProps -> ViewType.Existing
            is VocabEntryCreateProps -> ViewType.Create
            else -> error("Unhandled View Type for item: $item")
        }
    }

    private fun getItemAt(position: Int) = items[position]

    private object ViewType {
        const val Existing = 0
        const val Create = 1
    }

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<VocabEntryViewProps>() {
            override fun areItemsTheSame(
                oldProps: VocabEntryViewProps,
                newProps: VocabEntryViewProps
            ) = oldProps.entryItem.isSameItem(newProps.entryItem)

            override fun areContentsTheSame(
                oldEditItem: VocabEntryViewProps,
                newEditItem: VocabEntryViewProps
            ) = oldEditItem == newEditItem
        }
    }
}
