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
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemData
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingItemView

class VocabEntriesAdapter(
    vocabEntryExistingCallback: VocabEntryExistingCallback,
    vocabEntryCreateCallback: VocabEntryCreateCallback,
    tagCreateCallback: TagCreateCallback
) : DelegatedRecyclerAdapter<VocabEntryItemPresentationData, View>() {

    override val positionDelegate = LinearPositionDelegate(this, diffCallback)

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<VocabEntryItemPresentationData, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryExistingItemView(it.context).apply {
                        this.vocabEntryExistingCallback = vocabEntryExistingCallback
                    }
                },
                FeatureConfig<VocabEntryItemPresentationData.Existing, VocabEntryExistingItemView>().apply {
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
                FeatureConfig<VocabEntryItemPresentationData.Create, VocabEntryCreateItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            )
        )
    )

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val data = positionDelegate.getItemAt(position).data
        return when(data) {
            is VocabEntryItemData.Existing -> data.entryId
            is VocabEntryItemData.Create -> -1
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<VocabEntryItemPresentationData>() {
            override fun areItemsTheSame(oldItemPresentationData: VocabEntryItemPresentationData, newItemPresentationData: VocabEntryItemPresentationData) =
                when (oldItemPresentationData) {
                    /* There can only be 1 create item */
                    is VocabEntryItemPresentationData.Create -> newItemPresentationData is VocabEntryItemPresentationData.Create
                    is VocabEntryItemPresentationData.Existing ->
                        newItemPresentationData is VocabEntryItemPresentationData.Existing && oldItemPresentationData.data.entryId == newItemPresentationData.data.entryId
                }

            override fun areContentsTheSame(oldItemPresentationData: VocabEntryItemPresentationData, newItemPresentationData: VocabEntryItemPresentationData) =
                oldItemPresentationData == newItemPresentationData
        }
    }
}
