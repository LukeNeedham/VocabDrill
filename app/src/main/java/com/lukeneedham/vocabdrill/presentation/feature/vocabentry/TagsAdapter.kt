package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.adapter.DefaultDelegatedRecyclerAdapter
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureConfig
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.config.ItemTypeConfigListRegistry
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagExistingView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem

class TagsAdapter(
    context: Context,
    onExistingTagClick: (TagItem.Existing) -> Unit,
    onCreateTagNameChanged: ((tag: TagItem.Create, name: String, nameInputView: View) -> Unit),
) : DefaultDelegatedRecyclerAdapter<TagItem, View>() {

    override val itemTypeConfigRegistry = ItemTypeConfigListRegistry<TagItem, View>(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    TagCreateView(context).apply {
                        this.onNameChanged = onCreateTagNameChanged
                    }
                },
                FeatureConfig<TagItem.Create, TagCreateView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagItem.Existing, TagExistingView> {
                addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
                addOnItemClickListener { tag, _, _ -> onExistingTagClick(tag) }
            }
        )
    )
}
