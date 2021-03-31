package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.presentation.feature.tag.*
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.ListDiffCallback
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.ViewHolder
import com.lukeneedham.vocabdrill.util.extension.TAG

class TagsAdapter(
    private val onExistingTagClick: (TagItemProps.Existing) -> Unit,
    private val tagCreateViewCallback: TagCreateViewCallback,
) : RecyclerView.Adapter<ViewHolder>() {

    private var itemHeight: Int = 0

    private var items: List<TagItemProps> = emptyList()

    init {
        setHasStableIds(true)
    }

    fun getItems() = items

    /**
     * @param refresh controls whether to rebind for this new data, overriding item view internal state.
     * If [refresh] is false, internal state of the item view will be preserved instead.
     * This is an optimisation - by passing [refresh] as false when internal item view state is reliable,
     * unnecessary rebinds can be avoided and performance improved.
     */
    fun submitList(items: List<TagItemProps>, refresh: Boolean) {
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
            ViewType.Existing -> TagExistingView(parent.context)
            ViewType.Create -> TagCreateView(parent.context).apply {
                this.callback = tagCreateViewCallback
            }
            else -> error("Unhandled viewType: $viewType")
        }
        //view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemAt(position)
        val itemView = holder.itemView
        when (item) {
            is TagItemProps.Existing -> {
                itemView as TagExistingView
                itemView.setItem(position, item)
                itemView.setOnClickListener {
                    onExistingTagClick(item)
                }
            }
            is TagItemProps.Create -> {
                itemView as TagCreateView
                itemView.setItem(position, item)
            }
        }
        updateItemLayoutParams(holder.itemView)
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long {
        val item = getItemAt(position)
        return when (item) {
            is TagItemProps.Existing -> item.data.id
            is TagItemProps.Create -> Long.MAX_VALUE
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItemAt(position)
        return when (item) {
            is TagItemProps.Existing -> ViewType.Existing
            is TagItemProps.Create -> ViewType.Create
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        updateItemLayoutParams(holder.itemView)
    }

    fun setItemHeight(height: Int) {
        itemHeight = height
        notifyDataSetChanged()
    }

    private fun getItemAt(position: Int) = items[position]

    private fun updateItemLayoutParams(itemView: View) {
        fun doUpdate() {
            itemView.updateLayoutParams {
                if (itemHeight == 0) {
                    Log.i(TAG, "Item height is 0, view will not be visible")
                }
                height = itemHeight
            }
        }

        if (itemView.layoutParams == null) {
            itemView.post { doUpdate() }
        } else {
            doUpdate()
        }
    }

    private object ViewType {
        const val Existing = 0
        const val Create = 1
    }

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<TagItemProps>() {
            override fun areItemsTheSame(
                oldProps: TagItemProps,
                newProps: TagItemProps
            ) = oldProps.isSameItem(newProps)

            override fun areContentsTheSame(
                oldEditItem: TagItemProps,
                newEditItem: TagItemProps
            ) = oldEditItem == newEditItem
        }
    }
}
