package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.updateLayoutParams
import com.lukeneedham.flowerpotrecycler.adapter.DefaultDelegatedRecyclerAdapter
import com.lukeneedham.flowerpotrecycler.adapter.ViewHolder
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.config.ItemTypeConfigListRegistry
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagExistingView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.util.extension.TAG

/** Requires use of flexbox layout manager */
class TagsAdapter(
    context: Context,
    onExistingTagClick: (TagItem.Existing) -> Unit,
    onCreateTagNameChanged: ((tag: TagItem.Create, name: String, nameInputView: View) -> Unit),
) : DefaultDelegatedRecyclerAdapter<TagItem, View>() {

    private var itemHeight: Int = 0

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<TagItem, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    TagCreateView(context).apply {
                        this.onNameChanged = onCreateTagNameChanged
                    }
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagItem.Existing, TagExistingView> {
                addOnItemClickListener { tag, _, _ -> onExistingTagClick(tag) }
            }
        )
    )

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
        if (itemView.layoutParams == null) {
            Log.i(TAG, "Layout params not yet attached - skipping")
            return
        }
        itemView.updateLayoutParams {
            if (itemHeight == 0) {
                Log.i(TAG, "Item height is 0, view will not be visible")
            }
            height = itemHeight
        }
    }
}
