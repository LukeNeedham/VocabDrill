package com.lukeneedham.vocabdrill.presentation.feature.language

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.adapter.DelegatedRecyclerAdapter
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureConfig
import com.lukeneedham.flowerpotrecycler.adapter.delegates.position.implementation.LinearPositionDelegate
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.config.ItemTypeConfigListRegistry
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingItemView

class VocabEntriesAdapter(
    vocabEntryExistingCallback: VocabEntryExistingCallback,
    vocabEntryCreateCallback: VocabEntryCreateCallback,
    tagCreateCallback: TagCreateCallback
) : DelegatedRecyclerAdapter<VocabEntryItem, View>() {

    override val positionDelegate = LinearPositionDelegate(this, diffCallback)

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<VocabEntryItem, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryExistingItemView(it.context).apply {
                        this.vocabEntryExistingCallback = vocabEntryExistingCallback
                    }
                },
                FeatureConfig<VocabEntryItem.Existing, VocabEntryExistingItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            ),
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryCreateItemView(it.context).apply {
                        this.vocabEntryCreateCallback = vocabEntryCreateCallback
                        this.tagCreateCallback = tagCreateCallback
                    }
                },
                FeatureConfig<VocabEntryItem.Create, VocabEntryCreateItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            )
        ),
    )

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<VocabEntryItem>() {
            override fun areItemsTheSame(oldItem: VocabEntryItem, newItem: VocabEntryItem) =
                when (oldItem) {
                    /* There can only be 1 create item */
                    is VocabEntryItem.Create -> newItem is VocabEntryItem.Create
                    is VocabEntryItem.Existing ->
                        newItem is VocabEntryItem.Existing && oldItem.entryId == newItem.entryId
                }

            /**
             * The item view itself ensures that 
             * "the items' visual representations are the same" for most things - except tags
             */
            override fun areContentsTheSame(oldItem: VocabEntryItem, newItem: VocabEntryItem) =
                oldItem.tags == newItem.tags
        }
    }
}
