package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import com.lukeneedham.flowerpotrecycler.adapter.DefaultDelegatedRecyclerAdapter
import com.lukeneedham.flowerpotrecycler.adapter.ViewHolder
import com.lukeneedham.flowerpotrecycler.adapter.delegates.position.implementation.LinearPositionDelegate
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.config.ItemTypeConfigListRegistry
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.presentation.feature.tag.*
import com.lukeneedham.vocabdrill.util.extension.TAG

/** Requires use of flexbox layout manager */
class TagsAdapter(
    context: Context,
    onExistingTagClick: (TagPresentItem.Existing) -> Unit,
    tagCreateViewCallback: TagCreateViewCallback,
) : DefaultDelegatedRecyclerAdapter<TagPresentItem, View>() {

    private var itemHeight: Int = 0

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<TagPresentItem, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    TagCreateView(context).apply {
                        this.callback = tagCreateViewCallback
                    }
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagPresentItem.Existing, TagExistingView> {
                addOnItemClickListener { tag, _, _ -> onExistingTagClick(tag) }
            }
        )
    )

    override val positionDelegate = LinearPositionDelegate(this, diffCallback)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val tagItem = positionDelegate.getItemAt(position)
        return when (tagItem) {
            is TagPresentItem.Existing -> tagItem.data.id
            is TagPresentItem.Create -> Long.MAX_VALUE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<View>, position: Int) {
        super.onBindViewHolder(holder, position)
        updateItemLayoutParams(holder.itemView)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder<View>) {
        super.onViewAttachedToWindow(holder)
        updateItemLayoutParams(holder.itemView)
    }

    fun setItemHeight(height: Int) {
        itemHeight = height
        notifyDataSetChanged()
    }

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

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<TagPresentItem>() {
            override fun areItemsTheSame(
                oldItem: TagPresentItem,
                newItem: TagPresentItem
            ) = oldItem.isSameItem(newItem)

            override fun areContentsTheSame(
                oldItem: TagPresentItem,
                newItem: TagPresentItem
            ) = oldItem == newItem
        }
    }
}
