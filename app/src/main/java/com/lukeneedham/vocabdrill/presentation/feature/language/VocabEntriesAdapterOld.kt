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

class VocabEntriesAdapterOld(
    vocabEntryExistingCallback: VocabEntryExistingCallback,
    vocabEntryCreateCallback: VocabEntryCreateCallback,
    tagCreateCallback: TagCreateCallback,
    tagExistingClickListener: (entryItem: VocabEntryEditItem, tag: TagItemProps.Existing) -> Unit,
    tagSuggestionClickListener: (entryItem: VocabEntryEditItem, suggestion: TagSuggestion) -> Unit
) : DelegatedRecyclerAdapter<VocabEntryViewProps, View>() {

    override val positionDelegate = LinearPositionDelegate(this, diffCallback)

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<VocabEntryViewProps, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryExistingItemView(it.context).apply {
                        this.vocabEntryExistingCallback = vocabEntryExistingCallback
                        this.tagCreateCallback = tagCreateCallback
                        this.tagExistingClickListener = tagExistingClickListener
                        this.tagSuggestionClickListener = tagSuggestionClickListener
                    }
                },
                FeatureConfig<VocabEntryExistingProps, VocabEntryExistingItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            ),
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryCreateItemView(it.context).apply {
                        this.vocabEntryCreateCallback = vocabEntryCreateCallback
                        this.tagCreateCallback = tagCreateCallback
                        this.tagExistingClickListener = tagExistingClickListener
                        this.tagSuggestionClickListener = tagSuggestionClickListener
                    }
                },
                FeatureConfig<VocabEntryCreateProps, VocabEntryCreateItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            )
        )
    )

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val entryOrCreate = positionDelegate.getItemAt(position)
        return when (entryOrCreate) {
            is VocabEntryExistingProps -> entryOrCreate.entryItem.entry.id
            is VocabEntryCreateProps -> Long.MAX_VALUE
            else -> error("Unexpected item type: $entryOrCreate")
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<VocabEntryViewProps>() {
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
